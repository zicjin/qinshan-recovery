package com.sunwayworld.cloud.module.lcdp.checkinrecord.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.checkinconfig.bean.LcdpCheckinConfigBean;
import com.sunwayworld.cloud.module.lcdp.checkinconfig.service.LcdpCheckinConfigService;
import com.sunwayworld.cloud.module.lcdp.checkinrecord.bean.LcdpCheckinRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkinrecord.persistent.dao.LcdpCheckinRecordDao;
import com.sunwayworld.cloud.module.lcdp.checkinrecord.service.LcdpCheckinRecordService;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckOutDTO;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCrdLogBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.service.LcdpCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.service.LcdpCrdLogService;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpExportLogFileDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.HttpErrorMessage;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Repository
@GikamBean
public class LcdpCheckinRecordServiceImpl implements LcdpCheckinRecordService {
   private static final Logger log = LoggerFactory.getLogger(LcdpCheckinRecordServiceImpl.class);
   @Autowired
   private LcdpCheckinRecordDao lcdpCheckinRecordDao;
   @Autowired
   private CoreFileService coreFileService;
   @Autowired
   private LcdpCheckinConfigService checkinConfigService;
   @Autowired
   private LcdpResourceService resourceService;
   @Autowired
   private LcdpCheckoutRecordService checkoutRecordService;
   @Autowired
   private LcdpCrdLogService crdLogService;
   @Autowired
   private CoreFileManager fileManager;

   public LcdpCheckinRecordDao getDao() {
      return this.lcdpCheckinRecordDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LcdpCheckinRecordBean lcdpCheckinRecord = jsonWrapper.parseUnique(LcdpCheckinRecordBean.class);
      lcdpCheckinRecord.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(lcdpCheckinRecord);
      return lcdpCheckinRecord.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void receive(MultipartFile file, String checkoutRecordStr) {
      String license = ServletUtils.getCurrentRequest().getHeader("license");
      String serverAddress = ServletUtils.getCurrentRequest().getHeader("serverAddress");
      List<LcdpCheckinConfigBean> checkinConfigList = this.checkinConfigService.selectAll();
      List<LcdpCheckinConfigBean> serverAddressList = checkinConfigList.stream().map(cc -> {
         String systemUrl = cc.getSystemUrl();
         if (systemUrl.endsWith("/")) {
            systemUrl = systemUrl.substring(0, systemUrl.length() - 1);
         }

         cc.setSystemUrl(systemUrl);
         return (LcdpCheckinConfigBean)cc;
      }).filter(cc -> StringUtils.equals(serverAddress, cc.getSystemUrl().substring(cc.getSystemUrl().indexOf("//")))).collect(Collectors.toList());
      List<LcdpCheckinConfigBean> authList = serverAddressList.stream()
         .filter(config -> StringUtils.equals(config.getLicense(), license) && StringUtils.equals("1", config.getActivatedFlag()))
         .collect(Collectors.toList());
      if (authList.isEmpty()) {
         ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, I18nHelper.getMessage("GIKAM.EXCEPTION.AUTH_FAILD")));
      } else {
         LcdpCheckinConfigBean checkinConfig = authList.stream().findFirst().orElse(null);
         LcdpCheckoutRecordBean checkoutRecord = JSONObject.parseObject(checkoutRecordStr, LcdpCheckoutRecordBean.class);
         LcdpCheckinRecordBean checkinRecord = new LcdpCheckinRecordBean();
         checkinRecord.setId(ApplicationContextHelper.getNextIdentity());
         checkinRecord.setCheckoutRecordId(checkoutRecord.getId());
         checkinRecord.setCheckinNo(ApplicationContextHelper.getNextSequence("T_LCDP_CHECKIN_RECORD"));
         checkinRecord.setCheckinNote(checkoutRecord.getCheckoutNote());
         checkinRecord.setCheckoutRecordNo(checkoutRecord.getCheckoutNo());
         checkinRecord.setCheckinWay("checkin");
         checkinRecord.setCheckinStatus("notCheckin");
         checkinRecord.setCheckoutSysName(StringUtils.isEmpty(checkinConfig.getSystemName()) ? serverAddress.substring(2) : checkinConfig.getSystemName());
         this.getDao().insert(checkinRecord);
         Long key = ApplicationContextHelper.getNextIdentity();
         CoreFileBean insertCoreFile = new CoreFileBean();
         insertCoreFile.setId(key);
         insertCoreFile.setTargetId(this.getDao().getTable() + "$" + checkinRecord.getId());
         insertCoreFile.setName(this.getOriginalFilename(file.getOriginalFilename()));
         insertCoreFile.setFileExt(FileUtils.getFileExtension(insertCoreFile.getName()));
         insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(key + "$" + insertCoreFile.getName()));
         insertCoreFile.setScope(FileScope.temp.name());
         insertCoreFile.setSize(file.getSize());
         insertCoreFile.setTimes(0L);
         insertCoreFile.setPermanent("1");
         insertCoreFile.setDownloadable("1");
         insertCoreFile.setCreatedTime(LocalDateTime.now());
         this.fileManager.upload(insertCoreFile, file);
         this.coreFileService.getDao().insert(insertCoreFile);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void importFile(RestJsonWrapperBean jsonWrapper) {
      Map<String, String> paramMap = jsonWrapper.getParamMap();
      Long fileId = Long.valueOf(paramMap.get("fileId"));
      CoreFileBean coreFileBean = this.coreFileService.selectById(fileId);
      LcdpCheckinRecordBean checkinRecord = new LcdpCheckinRecordBean();
      checkinRecord.setId(ApplicationContextHelper.getNextIdentity());
      checkinRecord.setCheckinWay("import");
      checkinRecord.setCheckinNote(paramMap.get("checkinNote"));
      checkinRecord.setCheckoutRecordNo(paramMap.get("checkoutRecordNo"));
      checkinRecord.setCheckinStatus("notCheckin");
      checkinRecord.setCheckinNo(ApplicationContextHelper.getNextSequence("T_LCDP_CHECKIN_RECORD"));
      this.getDao().insert(checkinRecord);
      coreFileBean.setTargetId(this.getDao().getTable() + "$" + checkinRecord.getId());
      this.coreFileService.getDao().update(coreFileBean, "TARGETID");
   }

   @Override
   public LcdpExportLogFileDTO checkinAnalyse(RestJsonWrapperBean jsonWrapper) {
      LcdpExportLogFileDTO exportLogFileDTO = new LcdpExportLogFileDTO();
      LcdpCheckinRecordBean checkinRecord = jsonWrapper.parseUnique(LcdpCheckinRecordBean.class);
      CoreFileBean coreFileBean = this.coreFileService.selectFileList(this.getDao().getTable(), checkinRecord.getId()).stream().findFirst().get();
      Path filePath = CoreFileUtils.getLocalPath(coreFileBean);
      Map<String, String> dataMap = new HashMap<>();
      Map<String, String> fileMap = this.unZip(filePath, dataMap);
      String exportLog = fileMap.get("exportLog");
      JSONObject exportLogJson = JSON.parseObject(exportLog);
      List<LcdpResourceTreeNodeDTO> treeNodeDTOList = (List<LcdpResourceTreeNodeDTO>)exportLogJson.get("treeNodeDTOList");
      exportLogFileDTO.setFileId(coreFileBean.getId());
      exportLogFileDTO.setTreeNodeDTOList(treeNodeDTOList);
      exportLogFileDTO.setLog(exportLogJson.getString("log"));
      return exportLogFileDTO;
   }

   @Transactional
   @Override
   public String checkin(RestJsonWrapperBean jsonWrapper) {
      String checkinIdStr = jsonWrapper.getParamValue("checkinId");
      String importLog = jsonWrapper.getParamValue("importLog");
      Long checkinId = Long.valueOf(checkinIdStr);
      LcdpCheckinRecordBean checkinRecord = this.selectById(checkinId);
      String importResult = this.resourceService.importData(jsonWrapper);
      checkinRecord.setCheckinTime(LocalDateTime.now());
      checkinRecord.setCheckinNote(importLog);
      checkinRecord.setCheckinStatus("checkin");
      checkinRecord.setCreatedById(LocalContextHelper.getLoginUserId());
      checkinRecord.setCreatedByName(LocalContextHelper.getLoginUserName());
      this.getDao().update(checkinRecord, new String[]{"checkinStatus", "checkinTime", "checkinNote", "createdById", "createdByName"});
      return importResult;
   }

   @Override
   public LcdpCheckOutDTO export(RestJsonWrapperBean jsonWrapper) {
      LcdpCheckOutDTO checkOutDTO = new LcdpCheckOutDTO();
      LcdpCheckinRecordBean checkinRecord = jsonWrapper.parseUnique(LcdpCheckinRecordBean.class);
      LcdpCheckoutRecordBean checkoutRecord = this.checkoutRecordService
         .selectListByFilter(SearchFilter.instance().match("CHECKINRECORDID", checkinRecord.getId()).filter(MatchPattern.EQ), new Order[0])
         .stream()
         .findFirst()
         .orElse(null);
      CoreFileBean coreFileBean = this.coreFileService.selectFileListByTargetIds(this.getDao().getTable() + checkinRecord.getId()).stream().findFirst().get();
      Path localPath = CoreFileUtils.getLocalPath(coreFileBean);
      Map<String, String> dataMap = new HashMap<>();
      Map<String, String> fileMap = this.unZip(localPath, dataMap);
      String exportLog = fileMap.get("exportLog");
      JSONObject exportLogJson = JSON.parseObject(exportLog);
      if (checkoutRecord == null) {
         LcdpCheckoutRecordBean newCheckoutRecord = new LcdpCheckoutRecordBean();
         newCheckoutRecord.setId(ApplicationContextHelper.getNextIdentity());
         newCheckoutRecord.setCheckinRecordId(checkinRecord.getId());
         newCheckoutRecord.setCheckoutNo(ApplicationContextHelper.getNextSequence("T_LCDP_CHECKOUT_RECORD"));
         newCheckoutRecord.setCheckoutNote(checkinRecord.getCheckinNote());
         newCheckoutRecord.setContent(exportLogJson.get("treeNodeDTOList").toString());
         this.checkoutRecordService.getDao().insert(newCheckoutRecord);
         LcdpCrdLogBean crdLog = new LcdpCrdLogBean();
         crdLog.setId(ApplicationContextHelper.getNextIdentity());
         crdLog.setCheckoutRecordId(newCheckoutRecord.getId());
         crdLog.setCheckoutType("export");
         this.crdLogService.getDao().insert(crdLog);
         this.coreFileService
            .copy(this.getDao().getTable() + checkinRecord.getId(), this.checkoutRecordService.getDao().getTable() + newCheckoutRecord.getId());
         CoreFileBean newCoreFile = this.coreFileService
            .selectFileListByTargetIds(this.checkoutRecordService.getDao().getTable() + newCheckoutRecord.getId())
            .stream()
            .findFirst()
            .get();
         String downloadUrl = this.fileManager.getAbsoluteDownloadUrl(newCoreFile);
         checkOutDTO.setDownLoadUrl(downloadUrl);
      } else {
         LcdpCrdLogBean crdLog = new LcdpCrdLogBean();
         crdLog.setId(ApplicationContextHelper.getNextIdentity());
         crdLog.setCheckoutRecordId(checkoutRecord.getId());
         crdLog.setCheckoutType("export");
         this.crdLogService.getDao().insert(crdLog);
         CoreFileBean newCoreFile = this.coreFileService
            .selectFileListByTargetIds(this.checkoutRecordService.getDao().getTable() + checkoutRecord.getId())
            .stream()
            .findFirst()
            .get();
         String downloadUrl = this.fileManager.getAbsoluteDownloadUrl(newCoreFile);
         checkOutDTO.setDownLoadUrl(downloadUrl);
      }

      return checkOutDTO;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public LcdpCheckOutDTO checkout(RestJsonWrapperBean jsonWrapper) {
      LcdpCheckOutDTO checkOutDTO = new LcdpCheckOutDTO();
      LcdpCheckinRecordBean checkinRecord = jsonWrapper.parseUnique(LcdpCheckinRecordBean.class);
      List<LcdpCrdLogBean> crdLogList = jsonWrapper.parse(LcdpCrdLogBean.class);
      LcdpCheckoutRecordBean checkoutRecord = this.checkoutRecordService
         .selectListByFilter(SearchFilter.instance().match("CHECKINRECORDID", checkinRecord.getId()).filter(MatchPattern.EQ), new Order[0])
         .stream()
         .findFirst()
         .orElse(null);
      CoreFileBean coreFileBean = this.coreFileService.selectFileListByTargetIds(this.getDao().getTable() + checkinRecord.getId()).stream().findFirst().get();
      Path localPath = CoreFileUtils.getLocalPath(coreFileBean);
      Map<String, String> dataMap = new HashMap<>();
      Map<String, String> fileMap = this.unZip(localPath, dataMap);
      String exportLog = fileMap.get("exportLog");
      JSONObject exportLogJson = JSON.parseObject(exportLog);
      if (checkoutRecord == null) {
         LcdpCheckoutRecordBean newCheckoutRecord = new LcdpCheckoutRecordBean();
         newCheckoutRecord.setId(ApplicationContextHelper.getNextIdentity());
         newCheckoutRecord.setCheckinRecordId(checkinRecord.getId());
         newCheckoutRecord.setCheckoutNote(checkinRecord.getCheckinNote());
         newCheckoutRecord.setCheckoutNo(ApplicationContextHelper.getNextSequence("T_LCDP_CHECKOUT_RECORD"));
         newCheckoutRecord.setContent(exportLogJson.get("treeNodeDTOList").toString());
         this.checkoutRecordService.getDao().insert(newCheckoutRecord);
         this.coreFileService
            .copy(this.getDao().getTable() + checkinRecord.getId(), this.checkoutRecordService.getDao().getTable() + newCheckoutRecord.getId());
         crdLogList.forEach(log -> {
            log.setId(ApplicationContextHelper.getNextIdentity());
            log.setCheckoutRecordId(newCheckoutRecord.getId());
         });
         this.checkoutRecordService.doCheckout(crdLogList, checkOutDTO, checkoutRecord);
         this.crdLogService.getDao().insert(crdLogList);
      } else {
         crdLogList.forEach(log -> {
            log.setId(ApplicationContextHelper.getNextIdentity());
            log.setCheckoutRecordId(checkoutRecord.getId());
         });
         this.checkoutRecordService.doCheckout(crdLogList, checkOutDTO, checkoutRecord);
         this.crdLogService.getDao().insert(crdLogList);
      }

      return checkOutDTO;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void ignore(RestJsonWrapperBean jsonWrapper) {
      LcdpCheckinRecordBean checkinRecord = jsonWrapper.parseUnique(LcdpCheckinRecordBean.class);
      LcdpCheckinRecordBean queryCheckinRecord = this.selectById(checkinRecord.getId());
      if (StringUtils.equals("checkin", queryCheckinRecord.getCheckinStatus())) {
         throw new CheckedException("GIKAM.EXCEPTION.NOT_ALLOWED_CHANGE_STATUS");
      } else {
         this.getDao().update(checkinRecord, new String[]{"checkinStatus"});
      }
   }

   @Override
   public RestValidationResultBean checkinValidator(RestJsonWrapperBean jsonWrapper) {
      RestValidationResultBean resultBean = new RestValidationResultBean(true);
      String checkinIdStr = jsonWrapper.getParamValue("checkinId");
      CoreFileBean coreFileBean = this.coreFileService.selectFileListByTargetIds("T_LCDP_CHECKIN_RECORD$" + checkinIdStr).stream().findFirst().get();
      Path filePath = CoreFileUtils.getLocalPath(coreFileBean);
      List<LcdpResourceVersionBean> resourceVersionList = jsonWrapper.parse(LcdpResourceVersionBean.class);
      if (ObjectUtils.isEmpty(resourceVersionList)) {
         return new RestValidationResultBean(true);
      } else {
         List<LcdpResourceBean> sourceResourceList = new ArrayList<>();
         List<String> tableNameList = new ArrayList<>();
         List<String> viewNameList = new ArrayList<>();
         List<String> resourceNameList = new ArrayList<>();
         resourceVersionList.forEach(version -> {
            if ("table".equals(version.getResourceCategory())) {
               tableNameList.add(version.getResourceId());
               resourceNameList.add(version.getResourceId());
            } else if ("db-view".equals(version.getResourceCategory())) {
               viewNameList.add(version.getResourceId());
               resourceNameList.add(version.getResourceId());
            } else if (!"table".equals(version.getResourceName()) && !"db-view".equals(version.getResourceName())) {
               LcdpResourceBean resource = new LcdpResourceBean();

               try {
                  Long.valueOf(version.getResourceId());
               } catch (NumberFormatException var7x) {
                  throw new CheckedException(I18nHelper.getMessage("LCDP.EXCEPTION.NUMBERFORMATEXCEPTION" + version.getResourceId()));
               }

               resource.setId(Long.valueOf(version.getResourceId()));
               resource.setResourceName(version.getResourceName());
               resource.setResourceCategory(version.getResourceCategory());
               sourceResourceList.add(resource);
               resourceNameList.add(version.getResourceName());
            }
         });
         int importFileSize = sourceResourceList.size() + tableNameList.size() + viewNameList.size();
         if (resourceVersionList.size() != importFileSize) {
            log.info("这些文件没有了！！！！！！！！！！！！！！！！！！！！！！！！！");
            List<LcdpResourceVersionBean> deletionResouceList = resourceVersionList.stream()
               .filter(version -> !resourceNameList.contains(version.getResourceName()))
               .collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            deletionResouceList.forEach(v -> sb.append(v.getResourceName()).append("!!!!"));
            log.info("丢失的文件名列表！！！！！！！！" + sb.toString());
            throw new CheckedException(I18nHelper.getMessage("LCDP.EXCEPTION.IMPORT.FILE.DELETION.CONTACT.ADMIN"));
         } else {
            List<String> ignoreFileName = ArrayUtils.asList("exportLog", "codeCategory", "code", "adminSelectConfig", "menu", "sysClientJs", "checkoutConfig");
            Map<String, String> fileMap = new HashMap<>();
            fileMap = this.unZip(filePath, fileMap);
            List<String> sourceFileList = fileMap.keySet()
               .stream()
               .filter(fileNamex -> !ignoreFileName.contains(fileNamex))
               .filter(fileNamex -> !fileNamex.endsWith("view") && !fileNamex.endsWith("sysI18nDependence") && !fileNamex.endsWith("i18n"))
               .collect(Collectors.toList());
            if (importFileSize == sourceFileList.size()) {
               return resultBean;
            } else {
               Map<String, String> finalFileMap = new HashMap<>();
               finalFileMap.putAll(fileMap);
               StringBuilder record = new StringBuilder();
               List<String> resourceFileNameList = resourceVersionList.stream().map(LcdpResourceVersionBean::getResourceId).collect(Collectors.toList());
               List<String> unImportFileNameList = sourceFileList.stream()
                  .filter(fileNamex -> !resourceFileNameList.contains(fileNamex))
                  .collect(Collectors.toList());
               List<String> resourceFileNameFilterList = sourceFileList.stream()
                  .filter(
                     fileNamex -> !StringUtils.equals(JSON.parseObject(finalFileMap.get(fileNamex)).getString("resourceCategory"), "module")
                        && !StringUtils.equals(JSON.parseObject(finalFileMap.get(fileNamex)).getString("resourceCategory"), "category")
                  )
                  .collect(Collectors.toList());
               int unImportfileCount = 0;

               for (String fileName : unImportFileNameList) {
                  String fileJson = fileMap.get(fileName);
                  JSONObject jsonObject = JSON.parseObject(fileJson);
                  if (!StringUtils.equals(jsonObject.getString("resourceCategory"), "module")
                     && !StringUtils.equals(jsonObject.getString("resourceCategory"), "category")) {
                     unImportfileCount++;
                     String resourceName = !StringUtils.isEmpty(jsonObject.getString("resourceName"))
                        ? jsonObject.getString("resourceName")
                        : (!StringUtils.isEmpty(jsonObject.getString("tableName")) ? jsonObject.getString("tableName") : jsonObject.getString("viewName"));
                     record.append(resourceName).append("\r\n");
                  }
               }

               if (unImportfileCount != 0 && !StringUtils.isEmpty(record.toString())) {
                  record.insert(
                     0,
                     I18nHelper.getMessage("当前选择导入文件与导入文件有数量差异，原文件数量" + resourceFileNameFilterList.size() + "导入文件数量" + importFileSize) + "\r\n未选择导入文件名如下:\r\n"
                  );
                  log.info("查我查我查我查我！！！！！！！！！！！！！！！！！！！");
                  log.info(record.toString());
                  resultBean.setValid(false);
                  resultBean.setMessage(record.toString());
                  return resultBean;
               } else {
                  return resultBean;
               }
            }
         }
      }
   }

   private String getOriginalFilename(String filename) {
      int index = filename.lastIndexOf("\\");
      if (index >= 0) {
         return filename.substring(index + 1);
      } else {
         index = filename.lastIndexOf("/");
         return index > 0 ? filename.substring(index + 1) : filename;
      }
   }

   public Map<String, String> unZip(Path filePath, Map<String, String> dataMap) {
      FileInputStream input = null;
      ZipInputStream zipInputStream = null;

      try {
         input = new FileInputStream(filePath.toString());
         zipInputStream = new ZipInputStream(new BufferedInputStream(input), Charset.forName("GBK"));

         ZipEntry ze;
         while ((ze = zipInputStream.getNextEntry()) != null) {
            int i = ze.getName().indexOf("\\");
            if (i < 0) {
               i = ze.getName().indexOf("/");
            }

            String fileName = ze.getName().substring(i + 1);
            if (!ze.isDirectory()) {
               ByteArrayOutputStream baos = null;
               InputStream is = null;

               try {
                  baos = new ByteArrayOutputStream();
                  byte[] buffer = new byte[1024];

                  int len;
                  while ((len = zipInputStream.read(buffer)) > -1) {
                     baos.write(buffer, 0, len);
                  }

                  baos.flush();
                  is = new ByteArrayInputStream(baos.toByteArray());
                  String fileContent = StringUtils.read(is);
                  dataMap.put(fileName, EncryptUtils.base64Decode(fileContent));
               } finally {
                  if (is != null) {
                     try {
                        is.close();
                     } catch (IOException var44) {
                     }
                  }

                  if (baos != null) {
                     try {
                        baos.close();
                     } catch (IOException var43) {
                     }
                  }
               }
            }
         }
      } catch (IOException var46) {
         log.error(var46.getMessage(), (Throwable)var46);
         throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.EXCETION.FILE.ANALYSE.EXCETION"));
      } finally {
         if (zipInputStream != null) {
            try {
               zipInputStream.closeEntry();
            } catch (IOException var42) {
            }

            try {
               zipInputStream.close();
            } catch (IOException var41) {
            }
         }

         if (input != null) {
            try {
               input.close();
            } catch (IOException var40) {
            }
         }
      }

      return dataMap;
   }
}
