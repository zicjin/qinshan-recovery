package com.sunwayworld.module.item.file.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArchiveUtils;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.PdfUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.SpirePdfUtils;
import com.sunwayworld.framework.utils.SpireUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileCkeditorUploadResultDTO;
import com.sunwayworld.module.item.file.bean.CoreFileLogBean;
import com.sunwayworld.module.item.file.bean.CoreFileOfficeResultDTO;
import com.sunwayworld.module.item.file.bean.CoreOfficeDTO;
import com.sunwayworld.module.item.file.constant.CoreFileOfficeStatus;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import com.sunwayworld.module.item.file.consumer.CoreOfficeInvokeConsumer;
import com.sunwayworld.module.item.file.dao.CoreFileDao;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileEventService;
import com.sunwayworld.module.item.file.service.CoreFileLogService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@GikamBean
@Repository
public class CoreFileServiceImpl implements CoreFileService {
   private static final Logger logger = LogManager.getLogger(CoreFileService.class);
   @Autowired
   private CoreFileDao fileDao;
   @Autowired
   @Lazy
   private CoreFileService proxyInstance;
   @Autowired
   private CoreFileLogService fileLogService;
   @Autowired
   private CoreFileEventService fileEventService;
   @Lazy
   @Autowired
   private CoreFileManager fileManager;

   public CoreFileDao getDao() {
      return this.fileDao;
   }

   @Transactional(
      timeout = 200000
   )
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long upload(CoreFileBean coreFile, MultipartFile file) {
      Long key = ApplicationContextHelper.getNextIdentity();
      CoreFileBean insertCoreFile = new CoreFileBean();
      insertCoreFile.setId(key);
      insertCoreFile.setTargetId(this.getReplactedTargetId(coreFile.getTargetId()));
      insertCoreFile.setName(this.getOriginalFilename(file.getOriginalFilename()));
      insertCoreFile.setFileExt(FileUtils.getFileExtension(insertCoreFile.getName()));
      insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(key + "$" + insertCoreFile.getName()));
      insertCoreFile.setScope(Objects.toString(coreFile.getScope(), FileScope.secure.name()));
      insertCoreFile.setValidTimeFrom(coreFile.getValidTimeFrom());
      insertCoreFile.setValidTimeTo(coreFile.getValidTimeTo());
      insertCoreFile.setSize(file.getSize());
      insertCoreFile.setTimes(0L);
      insertCoreFile.setPermanent(Optional.ofNullable(coreFile.getPermanent()).orElse("1"));
      insertCoreFile.setExt(coreFile.getExt());
      insertCoreFile.setDownloadable(Optional.ofNullable(coreFile.getDownloadable()).orElse("1"));
      insertCoreFile.setCreatedTime(LocalDateTime.now());
      insertCoreFile.setBizCategory(coreFile.getBizCategory());
      insertCoreFile.setExt$(coreFile.getExt$());
      this.getDao().insert(insertCoreFile);
      this.fileManager.upload(insertCoreFile, file);
      this.fileLogService.insert(key, CoreFileOperation.INSERT.name());
      this.fileEventService.publishFileEvent(CoreFileOperation.INSERT, insertCoreFile);
      return key;
   }

   @Transactional(
      timeout = 200000
   )
   @Override
   public Long upload(CoreFileBean coreFile, File uploadFile) {
      Long key = ApplicationContextHelper.getNextIdentity();
      CoreFileBean insertCoreFile = new CoreFileBean();
      insertCoreFile.setId(key);
      insertCoreFile.setTargetId(this.getReplactedTargetId(coreFile.getTargetId()));
      insertCoreFile.setName(uploadFile.getName());
      insertCoreFile.setFileExt(FileUtils.getFileExtension(uploadFile.getName()));
      insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(key + "$" + uploadFile.getName()));
      insertCoreFile.setScope(Objects.toString(coreFile.getScope(), FileScope.secure.name()));
      insertCoreFile.setValidTimeFrom(coreFile.getValidTimeFrom());
      insertCoreFile.setValidTimeTo(coreFile.getValidTimeTo());
      insertCoreFile.setSize(uploadFile.length());
      insertCoreFile.setTimes(0L);
      insertCoreFile.setPermanent(Optional.ofNullable(coreFile.getPermanent()).orElse("1"));
      insertCoreFile.setExt(coreFile.getExt());
      insertCoreFile.setDownloadable("1");
      insertCoreFile.setCreatedTime(LocalDateTime.now());
      insertCoreFile.setExt$(coreFile.getExt$());
      insertCoreFile.setBizCategory(coreFile.getBizCategory());
      this.getDao().insert(insertCoreFile);
      this.fileManager.upload(insertCoreFile, uploadFile);
      this.fileLogService.insert(key, CoreFileOperation.INSERT.name());
      this.fileEventService.publishFileEvent(CoreFileOperation.INSERT, insertCoreFile);
      return key;
   }

   @Override
   public Page<CoreFileBean> selectSearchablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<CoreFileBean> page = this.selectPagination(parameter, rowBounds);
      page.getRows().stream().filter(f -> "1".equals(f.getDownloadable())).forEach(f -> f.setDownloadUrl(this.fileManager.getDownloadUrl(f)));
      return page;
   }

   @Override
   public <ID extends Serializable> List<CoreFileBean> selectFileList(String table, ID itemId) {
      CoreFileBean searchTerms = new CoreFileBean();
      searchTerms.setTargetId(CoreFileUtils.getTargetId(table, itemId));
      List<CoreFileBean> fileList = this.getDao().selectList(searchTerms, new Order[0]);
      fileList.forEach(f -> f.setDownloadUrl(this.fileManager.getDownloadUrl(f)));
      return fileList;
   }

   @Override
   public <ID extends Serializable> List<CoreFileBean> selectFileList(String table, List<ID> idList) {
      List<String> targetIdList = idList.stream().map(e -> CoreFileUtils.getTargetId(table, (ID)e)).collect(Collectors.toList());
      return this.getDao().selectListByOneColumnValues(targetIdList, "TARGETID", new Order[0]);
   }

   @Override
   public List<CoreFileBean> selectFileListByTargetIds(String... targetIds) {
      if (targetIds != null && targetIds.length != 0) {
         List<CoreFileBean> searchFileList = Arrays.asList(targetIds).stream().map(t -> {
            CoreFileBean f = new CoreFileBean();
            f.setTargetId(t);
            return f;
         }).collect(Collectors.toList());
         List<CoreFileBean> fileList = this.getDao().selectList(searchFileList, ArrayUtils.asList("TARGETID"), CollectionUtils.emptyList(), new Order[0]);
         fileList.forEach(f -> f.setDownloadUrl(this.fileManager.getDownloadUrl(f)));
         return fileList;
      } else {
         return CollectionUtils.emptyList();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean jsonWrapper) {
      List<CoreFileBean> fileList = jsonWrapper.parse(CoreFileBean.class);
      if (!fileList.isEmpty()) {
         List<CoreFileBean> selectedFileList = this.selectListByIds(fileList.stream().map(e -> e.getId()).collect(Collectors.toList()));
         this.deleteList(selectedFileList);
         fileList.forEach(f -> this.fileEventService.publishFileEvent(CoreFileOperation.DELETE, f));
      }
   }

   @Transactional
   @Override
   public <ID extends Serializable> void delete(String table, ID itemId) {
      CoreFileBean filter = new CoreFileBean();
      filter.setTargetId(CoreFileUtils.getTargetId(table, itemId));
      List<CoreFileBean> fileList = this.getDao().selectList(filter, new Order[0]);
      if (!fileList.isEmpty()) {
         this.deleteList(fileList);
         fileList.forEach(f -> this.fileEventService.publishFileEvent(CoreFileOperation.DELETE, f));
      }
   }

   @Transactional
   @Override
   public <PK extends Serializable> void batchDelete(String table, List<PK> itemIdList) {
      List<String> targetIdList = itemIdList.stream().map(itemId -> CoreFileUtils.getTargetId(table, itemId)).collect(Collectors.toList());
      List<CoreFileBean> fileList = this.getDao().selectListByOneColumnValues(targetIdList, "TARGETID", new Order[0]);
      if (!fileList.isEmpty()) {
         this.deleteList(fileList);
         fileList.forEach(f -> this.fileEventService.publishFileEvent(CoreFileOperation.DELETE, f));
      }
   }

   @Transactional
   public void delete(Long id) {
      this.deleteList(Arrays.asList(this.selectById(id)));
   }

   @Transactional
   @Override
   public void deleteList(List<CoreFileBean> fileList) {
      if (!fileList.isEmpty()) {
         fileList.forEach(f -> f.setTargetId("X" + f.getTargetId()));
         this.getDao().update(fileList, new String[]{"TARGETID"});
         List<Long> fileIdList = fileList.stream().map(f -> f.getId()).collect(Collectors.toList());
         this.fileLogService.insert(fileIdList, CoreFileOperation.DELETE.name());
         fileList.forEach(f -> this.fileEventService.publishFileEvent(CoreFileOperation.DELETE, f));
      }
   }

   @Transactional
   @Override
   public void save(RestJsonWrapperBean jsonWrapper) {
      List<CoreFileBean> fileList = jsonWrapper.parse(CoreFileBean.class);
      if (!fileList.isEmpty()) {
         this.getDao().updateIfChanged(fileList);
         fileList.forEach(f -> this.fileEventService.publishFileEvent(CoreFileOperation.UPDATE, f));
      }
   }

   @Transactional
   @Override
   public List<CoreFileBean> copy(String fromTargetId, Consumer<CoreFileBean> converter) {
      CoreFileBean searchTerms = new CoreFileBean();
      searchTerms.setTargetId(fromTargetId);
      List<CoreFileBean> fromFileList = this.getDao().selectList(searchTerms, new Order[0]);
      if (fromFileList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         LocalDateTime now = LocalDateTime.now();
         List<Pair<CoreFileBean, CoreFileBean>> pairList = new ArrayList<>();
         List<CoreFileBean> targetFileList = new ArrayList<>();

         for (CoreFileBean fromFile : fromFileList) {
            CoreFileBean toFile = new CoreFileBean();
            BeanUtils.copyProperties(fromFile, toFile);
            converter.accept(toFile);
            toFile.setId(ApplicationContextHelper.getNextIdentity());
            toFile.setMd5Name(EncryptUtils.MD5Encrypt(toFile.getId() + "$" + toFile.getName()));
            toFile.setTimes(0L);
            toFile.setCreatedTime(now);
            targetFileList.add(toFile);
            pairList.add(Pair.of(fromFile, toFile));
         }

         this.getDao().insert(targetFileList);
         pairList.forEach(p -> {
            this.fileManager.copyFile(p.getFirst(), p.getSecond());
            this.fileEventService.publishFileEvent(CoreFileOperation.INSERT, p.getSecond());
         });
         List<Long> fileIdList = targetFileList.stream().map(f -> f.getId()).collect(Collectors.toList());
         this.fileLogService.insert(fileIdList, CoreFileOperation.INSERT.name());
         return targetFileList;
      }
   }

   @Transactional
   @Override
   public List<CoreFileBean> copy(String sourceTargetId, String destTargetId) {
      return this.copy(sourceTargetId, f -> f.setTargetId(destTargetId));
   }

   @Override
   public String pack(String table, String itemId, String func) {
      CoreFileBean searchTerms = new CoreFileBean();
      searchTerms.setTargetId(table + "$" + itemId);
      searchTerms.setDownloadable("1");
      List<CoreFileBean> fileList = this.getDao().selectList(searchTerms, new Order[0]);
      Function<String, List<CoreFileBean>> function = ApplicationContextHelper.getBean(func);
      if (function != null) {
         fileList.addAll(function.apply(itemId));
      }

      return this.pack(fileList);
   }

   @Override
   public String pack(String table, RestJsonWrapperBean jsonWrapper) {
      List<String> idList = jsonWrapper.parseId(String.class);
      if (idList.isEmpty()) {
         return null;
      } else {
         List<String> targetIdList = idList.stream().distinct().map(i -> table + "$" + i).collect(Collectors.toList());
         List<CoreFileBean> fileList = this.getDao().selectListByOneColumnValues(targetIdList, "TARGETID", new Order[0]);
         return fileList.isEmpty() ? null : this.pack(fileList);
      }
   }

   @Override
   public String pack(RestJsonWrapperBean jsonWrapper) {
      List<Long> idList = jsonWrapper.parseId(Long.class);
      if (idList.isEmpty()) {
         return null;
      } else {
         List<CoreFileBean> fileList = this.getDao().selectListByIds(idList, new Order[0]);
         return fileList.isEmpty() ? null : this.pack(fileList);
      }
   }

   @Override
   public CoreFileBean selectDetail(Long id) {
      CoreFileBean file = this.getDao().selectById(id);
      file.setDownloadUrl(this.fileManager.getDownloadUrl(file));
      return file;
   }

   @Override
   public CoreFileCkeditorUploadResultDTO ckeditorUpload(CoreFileBean coreFile, MultipartFile file) {
      coreFile.setScope(FileScope.open.name());
      Long id = this.proxyInstance.upload(coreFile, file);
      CoreFileBean uploadedFile = this.getDao().selectById(id);
      CoreFileCkeditorUploadResultDTO result = new CoreFileCkeditorUploadResultDTO();
      result.setUploaded(1);
      result.setFileName(uploadedFile.getName());
      String downloadUrl = this.fileManager.getDownloadUrl(uploadedFile);
      result.setUrl(StringUtils.startsWith(downloadUrl, "/") ? downloadUrl.replaceFirst("/", "") : downloadUrl);
      return result;
   }

   @Transactional(
      timeout = 200000
   )
   @Override
   public CoreFileOfficeResultDTO sunwayOfficeInvoke(Long fileId) {
      CoreFileOfficeResultDTO result = new CoreFileOfficeResultDTO();
      CoreFileBean file = this.getDao().selectById(fileId);

      try (
         Scanner scanner = new Scanner(ServletUtils.getCurrentRequest().getInputStream());
         Scanner scannerUseDelimiter = scanner.useDelimiter("\\A");
      ) {
         String body = scannerUseDelimiter.hasNext() ? scanner.next() : "";
         JSONObject sunwayOfficeJson = JSONObject.parseObject(body);
         int sunwayOfficeFileStatus = sunwayOfficeJson.getIntValue("status");
         if (CoreFileOfficeStatus.SAVE.getStatusCode() == sunwayOfficeFileStatus || CoreFileOfficeStatus.EXIT_SAVE.getStatusCode() == sunwayOfficeFileStatus) {
            boolean login = LocalContextHelper.isUserLogin();

            try {
               if (!login) {
                  String userId = (String)sunwayOfficeJson.getJSONArray("users").get(0);
                  userId = URLDecoder.decode(userId, "UTF-8");
                  LocalContextHelper.setUserLogin(new LoginUser(userId, ""));
               }

               String sunwayOfficeFileUrl = sunwayOfficeJson.getString("url");
               String officeIntranetUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.office.file.intranet.url", String.class);
               if (!StringUtils.isBlank(officeIntranetUrl)) {
                  sunwayOfficeFileUrl = officeIntranetUrl + sunwayOfficeFileUrl.substring(sunwayOfficeFileUrl.indexOf("/cache/files/"));
               }

               file.setCreatedTime(LocalDateTime.now());
               File tempFile = FileUtils.createTempFile();
               HttpClientManager.getInstance().download(sunwayOfficeFileUrl, tempFile);
               this.fileManager.upload(file, tempFile);
               if (CoreFileOfficeStatus.EXIT_SAVE.getStatusCode() == sunwayOfficeFileStatus) {
                  file.setVersion(file.getVersion() + 1L);
                  this.getDao().update(file, new String[]{"VERSION", "CREATEDTIME"});
               } else {
                  this.getDao().update(file, new String[]{"CREATEDTIME"});
               }

               this.fileLogService.insert(fileId, CoreFileOperation.UPDATE.name());
               String service = ServletUtils.getCurrentRequestParamMap().get("service");
               if (null != service) {
                  CoreOfficeInvokeConsumer officeInvokeFunction = ApplicationContextHelper.getBean(service);
                  CoreOfficeDTO officeDTO = new CoreOfficeDTO(fileId, tempFile.toPath());
                  officeInvokeFunction.accept(officeDTO);
               }
            } finally {
               if (!login) {
                  LocalContextHelper.removeUserLogin();
               }
            }
         }

         result.setError(0);
      } catch (IOException var57) {
         result.setError(1);
         logger.warn(var57.getMessage(), (Throwable)var57);
      }

      return result;
   }

   @Transactional(
      timeout = 200000
   )
   @Override
   public CoreFileOfficeResultDTO sunwayOfficeExternalInvoke() {
      CoreFileOfficeResultDTO result = new CoreFileOfficeResultDTO();
      result.setError(0);
      return result;
   }

   @Override
   public Page<CoreFileLogBean> selectLogSearchablePagination(Long id, RestJsonWrapperBean wrapper) {
      return this.fileLogService.selectPaginationByFilter(SearchFilter.instance().match("FILEID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public String convertHtmlToPdf(RestJsonWrapperBean wrapper) {
      String htmlContent = Objects.requireNonNull(wrapper.getParamValue("content"));
      String fileName = Objects.requireNonNull(wrapper.getParamValue("fileName"));
      if (!StringUtils.endsWithIgnoreCase(fileName, ".pdf")) {
         fileName = fileName + ".pdf";
      }

      FilePathDTO pdfFilePath = CoreFileUtils.toFilePath(FileScope.temp, fileName);
      Path pdfPath = FilePathManager.getLocalPath(pdfFilePath);
      SpirePdfUtils.htmlContentToPdf(htmlContent, pdfPath);
      this.fileManager.upload(pdfFilePath, pdfPath);
      return this.fileManager.getDownloadUrl(pdfFilePath);
   }

   @Override
   public String convertHtmlToWord(RestJsonWrapperBean wrapper) {
      String htmlContent = Objects.requireNonNull(wrapper.getParamValue("content"));
      String fileName = Objects.requireNonNull(wrapper.getParamValue("fileName"));
      if (!StringUtils.endsWithIgnoreCase(fileName, ".docx")) {
         fileName = fileName + ".docx";
      }

      FilePathDTO wordFilePath = CoreFileUtils.toFilePath(FileScope.temp, fileName);
      Path wordPath = FilePathManager.getLocalPath(wordFilePath);
      SpireUtils.htmlContentToWord(htmlContent, wordPath);
      this.fileManager.upload(wordFilePath, wordPath);
      return this.fileManager.getDownloadUrl(wordFilePath);
   }

   @Override
   public void mergePdfList(List<FilePathDTO> sourcePdfList, FilePathDTO targetPdf) {
      List<Path> pdfList = new ArrayList<>();
      sourcePdfList.forEach(s -> {
         Path path = FilePathManager.getLocalPath(s);
         this.fileManager.download(s, path);
         pdfList.add(path);
      });
      PdfUtils.mergePdfFiles(FilePathManager.getLocalPath(targetPdf), pdfList.toArray(new Path[0]));
   }

   protected String getReplactedTargetId(String targetId) {
      return StringUtils.contains(targetId, "[{loginUserId}]")
         ? StringUtils.replace(targetId, "[{loginUserId}]", LocalContextHelper.getLoginUserId())
         : targetId;
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

   private String pack(List<CoreFileBean> fileList) {
      FilePathDTO filePath = FilePathDTO.of(
         FileScope.temp.name(), LocalDateTime.now(), StringUtils.randomUUID(16), I18nHelper.getMessage("GIKAM.FILE.PACK.DEFAULT_FILE_NAME") + ".zip"
      );
      List<Path> zipPathList = new ArrayList<>();

      for (CoreFileBean coreFile : fileList) {
         zipPathList.add(CoreFileUtils.getLocalPath(coreFile));
      }

      ArchiveUtils.zip(FilePathManager.getLocalPath(filePath), zipPathList.toArray(new Path[zipPathList.size()]));
      this.fileManager.upload(filePath, FilePathManager.getLocalPath(filePath));
      return this.fileManager.getDownloadUrl(filePath);
   }
}
