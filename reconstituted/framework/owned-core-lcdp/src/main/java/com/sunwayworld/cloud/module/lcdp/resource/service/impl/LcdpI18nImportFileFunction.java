package com.sunwayworld.cloud.module.lcdp.resource.service.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpI18nDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageI18nBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageI18nService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nConfigBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.service.CoreI18nConfigService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nService;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("LcdpI18nImportFileFunction")
public class LcdpI18nImportFileFunction implements CoreFileImportFunction {
   @Autowired
   private CoreI18nService coreI18nService;
   @Autowired
   private CoreI18nMessageService coreI18nMessageService;
   @Autowired
   private CoreI18nConfigService coreI18nConfigService;
   @Autowired
   private LcdpModulePageI18nService lcdpModulePageI18nService;

   @Override
   public boolean test(CoreFileBean file, String service) {
      return true;
   }

   @Transactional
   public CoreFileImportResultDTO apply(CoreFileBean file) {
      CoreFileImportResultDTO CoreFileImportResultDTO = new CoreFileImportResultDTO();
      FilePathDTO fr = CoreFileUtils.toFilePath(file);
      String localPath = FilePathManager.getLocalPath(fr).toString();
      List<CoreI18nConfigBean> coreI18nConfigList = this.coreI18nConfigService.selectAll();
      Map<String, String> configMap = new HashMap<>();
      coreI18nConfigList.forEach(e -> configMap.put(e.getLocaleName(), e.getId()));
      List<LcdpI18nDTO> i18nList = new ArrayList<>();

      try (
         FileInputStream fileInputStream = new FileInputStream(localPath);
         InputStream is = new BufferedInputStream(fileInputStream);
         XSSFWorkbook wb = new XSSFWorkbook(is);
      ) {
         XSSFSheet sheet = wb.getSheetAt(0);
         int firstRowNum = sheet.getFirstRowNum();
         int lastRowNum = sheet.getLastRowNum();
         int columnNum = sheet.getRow(firstRowNum).getPhysicalNumberOfCells();

         for (int i = firstRowNum + 2; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            LcdpI18nDTO importI18n = new LcdpI18nDTO();
            XSSFCell code = row.getCell(0);
            if (code != null) {
               code.setCellType(CellType.STRING);
               importI18n.setCode(code.getStringCellValue());
            }

            for (int j = 1; j < columnNum; j++) {
               XSSFCell xssfCell = row.getCell(j);
               if (xssfCell != null) {
                  xssfCell.setCellType(CellType.STRING);
                  importI18n.setExt$Item(sheet.getRow(1).getCell(j).getStringCellValue(), xssfCell.getStringCellValue());
               }
            }

            i18nList.add(importI18n);
         }
      } catch (Exception var75) {
         throw new FileException(I18nHelper.getMessage("FILE.FORMAT.INCORRECT"));
      }

      MapperParameter parameter = new MapperParameter();
      List<CoreI18nBean> coreI18nBeanList = this.coreI18nService.selectAll();
      Map<String, CoreI18nBean> coreI18nMap = new HashMap<>();
      List<CoreI18nBean> insertList = new ArrayList<>();
      List<CoreI18nBean> updateList = new ArrayList<>();
      List<CoreI18nMessageBean> updateMessageList = new ArrayList<>();
      List<CoreI18nMessageBean> insertMessageList = new ArrayList<>();
      coreI18nBeanList.forEach(e -> coreI18nMap.put(e.getCode(), e));
      List<CoreI18nMessageBean> i18nMessageBeanList = this.coreI18nMessageService.selectList(parameter);
      List<LcdpModulePageI18nBean> lcdpI18nMessageList = this.lcdpModulePageI18nService.selectEffectivePageI18nMessage(parameter);
      Map<String, LcdpI18nDTO> lcdpI18nMap = new HashMap<>();
      List<LcdpModulePageI18nBean> updateLcdpMessageList = new ArrayList<>();
      List<LcdpModulePageI18nBean> insertLcdpMessageList = new ArrayList<>();
      lcdpI18nMessageList.forEach(e -> {
         LcdpI18nDTO i18n = lcdpI18nMap.get(e.getCode());
         if (i18n == null) {
            i18n = new LcdpI18nDTO();
            i18n.setCode(e.getCode());
            i18n.setModulePageHistoryId(e.getModulePageHistoryId());
            i18n.setId(e.getModulePageId());
            lcdpI18nMap.put(e.getCode(), i18n);
         }

         i18n.getMessageList().add(e);
      });
      i18nList.forEach(
         e -> {
            String codex = e.getCode();
            if (coreI18nMap.containsKey(codex)) {
               CoreI18nBean coreI18nBean = coreI18nMap.get(codex);
               String description = e.getExt$Item(I18nHelper.getMessage("T_CORE_I18N.DESCRIPTION"));
               if (!StringUtils.equals(coreI18nBean.getDescription(), description)) {
                  coreI18nBean.setDescription(description);
                  updateList.add(coreI18nBean);
               }

               List<CoreI18nMessageBean> coreI18nMessageCollection = i18nMessageBeanList.stream()
                  .filter(messagex -> StringUtils.equals(messagex.getExt$Item("CODE"), codex))
                  .collect(Collectors.toList());

               for (int i = 0; i < coreI18nConfigList.size(); i++) {
                  String localeName = coreI18nConfigList.get(i).getLocaleName();
                  String newValue = e.getExt$Item(localeName);
                  if (!StringUtils.isEmpty(newValue)) {
                     CoreI18nMessageBean coreI18nMessage = coreI18nMessageCollection.stream()
                        .filter(messagex -> StringUtils.equals(localeName, messagex.getExt$Item("LOCALENAME")))
                        .findFirst()
                        .orElse(null);
                     if (!ObjectUtils.isEmpty(coreI18nMessage)) {
                        String oldValue = coreI18nMessage.getMessage();
                        if (!StringUtils.equals(oldValue, newValue)) {
                           coreI18nMessage.setMessage(newValue);
                           updateMessageList.add(coreI18nMessage);
                        }
                     } else {
                        CoreI18nMessageBean coreI18nMessageBean = new CoreI18nMessageBean();
                        coreI18nMessageBean.setId(ApplicationContextHelper.getNextIdentity());
                        coreI18nMessageBean.setI18nId(coreI18nBean.getId());
                        coreI18nMessageBean.setMessage(newValue);
                        coreI18nMessageBean.setI18nConfigId(configMap.get(localeName));
                        insertMessageList.add(coreI18nMessageBean);
                     }
                  }
               }
            } else if (lcdpI18nMap.containsKey(codex)) {
               LcdpI18nDTO lcdpI18nDTO = lcdpI18nMap.get(codex);
               List<LcdpModulePageI18nBean> lcdpI18nMessageCollection = lcdpI18nDTO.getMessageList();

               for (int ix = 0; ix < coreI18nConfigList.size(); ix++) {
                  String localeName = coreI18nConfigList.get(ix).getLocaleName();
                  String newValue = e.getExt$Item(localeName);
                  if (!StringUtils.isEmpty(newValue)) {
                     LcdpModulePageI18nBean lcdpI18nMessage = lcdpI18nMessageCollection.stream()
                        .filter(messagex -> StringUtils.equals(localeName, messagex.getExt$Item("LOCALENAME")))
                        .findFirst()
                        .orElse(null);
                     if (ObjectUtils.isEmpty(lcdpI18nMessage)) {
                        LcdpModulePageI18nBean lcdpI18nMessageBean = new LcdpModulePageI18nBean();
                        lcdpI18nMessageBean.setId(ApplicationContextHelper.getNextIdentity());
                        lcdpI18nMessageBean.setModulePageId(lcdpI18nDTO.getId());
                        lcdpI18nMessageBean.setModulePageHistoryId(lcdpI18nDTO.getModulePageHistoryId());
                        lcdpI18nMessageBean.setMessage(newValue);
                        lcdpI18nMessageBean.setCode(codex);
                        lcdpI18nMessageBean.setI18nConfigId(configMap.get(localeName));
                        insertLcdpMessageList.add(lcdpI18nMessageBean);
                     } else {
                        String oldValue = lcdpI18nMessage.getMessage();
                        if (!StringUtils.equals(oldValue, newValue)) {
                           lcdpI18nMessage.setMessage(newValue);
                           updateLcdpMessageList.add(lcdpI18nMessage);
                        }
                     }
                  }
               }
            } else {
               Long coreI18nId = ApplicationContextHelper.getNextIdentity();
               CoreI18nBean i18nBean = new CoreI18nBean();
               i18nBean.setId(coreI18nId);
               String message = e.getExt$Item(I18nHelper.getMessage("T_CORE_I18N.IMPORTCHINESE"));
               String description = e.getExt$Item(I18nHelper.getMessage("T_CORE_I18N.DESCRIPTION"));
               String defaultMessage = e.getExt$Item(I18nHelper.getMessage("T_CORE_I18N.DEFAULTMESSAGE"));
               if (description != null) {
                  i18nBean.setDescription(description);
               } else {
                  i18nBean.setDescription(message);
               }

               if (defaultMessage != null) {
                  i18nBean.setDefaultMessage(defaultMessage);
               } else {
                  i18nBean.setDefaultMessage(message);
               }

               for (int ixx = 0; ixx < coreI18nConfigList.size(); ixx++) {
                  String localeName = coreI18nConfigList.get(ixx).getLocaleName();
                  String newValue = e.getExt$Item(localeName);
                  if (!StringUtils.isEmpty(newValue)) {
                     CoreI18nMessageBean cnMessage = new CoreI18nMessageBean();
                     cnMessage.setId(ApplicationContextHelper.getNextIdentity());
                     cnMessage.setI18nId(coreI18nId);
                     cnMessage.setMessage(newValue);
                     cnMessage.setI18nConfigId(configMap.get(localeName));
                     insertMessageList.add(cnMessage);
                  }
               }

               insertList.add(i18nBean);
            }
         }
      );
      if (!updateLcdpMessageList.isEmpty()) {
         this.lcdpModulePageI18nService.getDao().update(updateLcdpMessageList, "MESSAGE");
      }

      if (!updateMessageList.isEmpty()) {
         this.coreI18nMessageService.getDao().update(updateMessageList, "MESSAGE");
      }

      if (!insertList.isEmpty()) {
         this.coreI18nService.getDao().insert(insertList);
      }

      if (!insertMessageList.isEmpty()) {
         this.coreI18nMessageService.getDao().insert(insertMessageList);
      }

      if (!updateList.isEmpty()) {
         this.coreI18nService.getDao().update(updateList, "DESCRIPTION", "DEFAULTMESSAGE");
      }

      if (!insertLcdpMessageList.isEmpty()) {
         this.lcdpModulePageI18nService.getDao().insert(insertLcdpMessageList);
      }

      return CoreFileImportResultDTO;
   }
}
