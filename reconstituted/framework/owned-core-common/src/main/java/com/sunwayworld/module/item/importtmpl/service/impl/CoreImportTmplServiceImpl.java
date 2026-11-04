package com.sunwayworld.module.item.importtmpl.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplBean;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplConfigBean;
import com.sunwayworld.module.item.importtmpl.dao.CoreImportTmplDao;
import com.sunwayworld.module.item.importtmpl.service.CoreImportTmplConfigService;
import com.sunwayworld.module.item.importtmpl.service.CoreImportTmplService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import com.sunwayworld.module.sys.metadata.bean.CoreTableFieldDTO;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreImportTmplServiceImpl implements CoreImportTmplService {
   private static final Log logger = LogFactory.getLog(CoreImportTmplServiceImpl.class);
   @Autowired
   private CoreImportTmplDao coreImportTmplDao;
   @Autowired
   private MapDao mapDao;
   @Autowired
   private CoreImportTmplConfigService coreImportTmplConfigService;
   @Autowired
   private CoreFileService fileService;
   @Autowired
   private CoreCodeService codeService;
   @Autowired
   private CoreTableService tableService;
   @Autowired
   @Lazy
   private CoreFileManager coreFileManager;

   public CoreImportTmplDao getDao() {
      return this.coreImportTmplDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreImportTmplBean coreImportTmpl = jsonWrapper.parseUnique(CoreImportTmplBean.class);
      coreImportTmpl.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreImportTmpl);
      return coreImportTmpl.getId();
   }

   @Override
   public Page<CoreImportTmplConfigBean> selectTmplConfigPaginationByImportTmplId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreImportTmplConfigService.selectPaginationByFilter(SearchFilter.instance().match("IMPORTTMPLID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void initTmplConfig(Long id, RestJsonWrapperBean wrapper) {
      String tableName = this.selectById(id).getTableName();
      List<CoreTableFieldDTO> tableFieldDTOList = this.tableService.selectFieldListByTableName(tableName);
      List<CoreImportTmplConfigBean> importTmplConfigList = new ArrayList<>();

      for (int i = 0; i < tableFieldDTOList.size(); i++) {
         CoreTableFieldDTO tableFieldDTO = tableFieldDTOList.get(i);
         CoreImportTmplConfigBean importTmplConfigBean = new CoreImportTmplConfigBean();
         importTmplConfigBean.setId(ApplicationContextHelper.getNextIdentity());
         importTmplConfigBean.setImportTmplId(id);
         importTmplConfigBean.setDbField(tableFieldDTO.getFieldName());
         importTmplConfigBean.setDefaultValue(tableFieldDTO.getDefaultValue());
         importTmplConfigBean.setExcelColumn(tableFieldDTO.getFieldComment());
         importTmplConfigBean.setExcelColumnNo(i + 1);
         if (!StringUtils.contains(tableFieldDTO.getFieldType(), "DATE") && !StringUtils.contains(tableFieldDTO.getFieldType(), "TIME")) {
            importTmplConfigBean.setFieldType("string");
         } else {
            importTmplConfigBean.setFieldType("date");
         }

         importTmplConfigList.add(importTmplConfigBean);
      }

      if (!importTmplConfigList.isEmpty()) {
         this.coreImportTmplConfigService.getDao().insert(importTmplConfigList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertTmplConfig(Long id, RestJsonWrapperBean wrapper) {
      List<CoreImportTmplConfigBean> coreImportTmplConfigList = wrapper.parse(CoreImportTmplConfigBean.class);
      coreImportTmplConfigList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setImportTmplId(id);
         e.setFieldType("string");
      });
      this.coreImportTmplConfigService.getDao().insert(coreImportTmplConfigList);
      return coreImportTmplConfigList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteTmplConfig(Long id, RestJsonWrapperBean wrapper) {
      this.coreImportTmplConfigService.delete(wrapper);
   }

   @Transactional
   @Override
   public JSONObject importData(Long importTmplId, Long fileId) {
      CoreFileBean coreFile = this.fileService.selectDetail(fileId);
      File tempFile = FileUtils.createTempFile();
      this.coreFileManager.download(coreFile, tempFile.toPath());
      CoreImportTmplBean importTmpl = this.selectById(importTmplId);
      List<CoreImportTmplConfigBean> importTmplConfigList = this.coreImportTmplConfigService
         .selectListByFilter(SearchFilter.instance().match("IMPORTTMPLID", importTmplId).filter(MatchPattern.EQ), new Order[0]);
      List<Map<String, Object>> mapList = this.extractDataFromExcel(tempFile.getAbsolutePath(), importTmpl, importTmplConfigList);
      if (!mapList.isEmpty()) {
         mapList.forEach(map -> {
            if (!CollectionUtils.containsIgnoreCase((Map<String, Object>)map, "id")) {
               map.put("ID", ApplicationContextHelper.getNextIdentity());
            }
         });

         try {
            this.mapDao.insert(importTmpl.getTableName(), mapList);
         } catch (ConvertFailedException var9) {
            logger.error(var9.getMessage(), var9);
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.IMPORTTMPLS.TIP.INVALID_DATA"));
         }
      }

      FileUtils.deleteQuietly(tempFile);
      JSONObject result = new JSONObject();
      result.put("success", true);
      return result;
   }

   private List<Map<String, Object>> extractDataFromExcel(String filePath, CoreImportTmplBean importTmpl, List<CoreImportTmplConfigBean> importTmplConfigList) {
      try (Workbook wb = WorkbookFactory.create(new File(filePath))) {
         Sheet sheet = wb.getSheetAt(0);
         List<Map<String, Object>> resultList = new ArrayList<>();
         int endRowNum = sheet.getPhysicalNumberOfRows();
         List<String> codeCategoryIdList = importTmplConfigList.stream()
            .filter(ex -> "select".equals(ex.getFieldType()) && !StringUtils.isEmpty(ex.getDataSource()))
            .map(ex -> ex.getDataSource())
            .collect(Collectors.toList());
         List<CoreCodeBean> codeList = this.codeService
            .getDao()
            .selectListByOneColumnValues(codeCategoryIdList, "CODECATEGORYID", Arrays.asList("CODE", "CODENAME", "CODECATEGORYID"));

         for (int rowNum = importTmpl.getStartRowIndex() - 1; rowNum < endRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
               Map<String, Object> map = new HashMap<>();

               for (CoreImportTmplConfigBean importTmplConfig : importTmplConfigList) {
                  if (importTmplConfig.getExcelColumnNo() != null && !StringUtils.isEmpty(importTmplConfig.getDbField())) {
                     Cell cell = row.getCell(importTmplConfig.getExcelColumnNo() - 1);
                     Object cellValue = this.getCellValue(cell);
                     if (ObjectUtils.isEmpty(cellValue)) {
                        cellValue = importTmplConfig.getDefaultValue();
                     }

                     if ("select".equals(importTmplConfig.getFieldType()) && !StringUtils.isEmpty(importTmplConfig.getDataSource())) {
                        String cellValueStr = String.valueOf(cellValue);
                        CoreCodeBean code = codeList.stream()
                           .filter(ex -> importTmplConfig.getDataSource().equals(ex.getCodeCategoryId()) && cellValueStr.equals(ex.getCodeName()))
                           .findFirst()
                           .orElse(null);
                        if (code != null) {
                           cellValue = code.getCode();
                        }
                     } else if ("date".equals(importTmplConfig.getFieldType())) {
                        try {
                           cellValue = ConvertUtils.convert(cellValue, LocalDateTime.class);
                        } catch (ConvertFailedException var29) {
                           logger.error(var29.getMessage(), var29);
                           throw new InvalidDataException(
                              importTmplConfig.getExcelColumn() + I18nHelper.getMessage("CORE.MODULE.ITEM.IMPORTTMPLS.TIP.INVALID_DATA")
                           );
                        }
                     }

                     map.put(importTmplConfig.getDbField(), cellValue);
                  }
               }

               resultList.add(map);
            }
         }

         return resultList;
      } catch (IOException var32) {
         throw new FileException(var32);
      }
   }

   private Object getCellValue(Cell cell) {
      Object cellValue = "";
      if (cell != null) {
         switch (cell.getCellType()) {
            case STRING:
               cellValue = StringUtils.trim(cell.getRichStringCellValue().getString());
               break;
            case NUMERIC:
               if (DateUtil.isCellDateFormatted(cell)) {
                  cellValue = DateUtil.getJavaDate(cell.getNumericCellValue());
               } else {
                  cellValue = cell.getNumericCellValue();
               }
               break;
            case BOOLEAN:
               cellValue = ObjectUtils.toString(cell.getBooleanCellValue());
               break;
            case FORMULA:
               FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
               CellValue cv = evaluator.evaluate(cell);
               switch (cv.getCellType()) {
                  case STRING:
                     cellValue = StringUtils.trim(cv.getStringValue());
                     return cellValue;
                  case NUMERIC:
                     cellValue = "" + cv.getNumberValue();
                     return cellValue;
                  case BOOLEAN:
                     cellValue = ObjectUtils.toString(cv.getBooleanValue());
                     return cellValue;
                  default:
                     cellValue = null;
                     return cellValue;
               }
            default:
               cellValue = "";
         }
      }

      return cellValue;
   }
}
