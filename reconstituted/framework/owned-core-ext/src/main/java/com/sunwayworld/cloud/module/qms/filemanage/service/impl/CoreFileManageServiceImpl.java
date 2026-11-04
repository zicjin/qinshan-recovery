package com.sunwayworld.cloud.module.qms.filemanage.service.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageAuthBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManagePrintRecorBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageReadRecordBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageVersionBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManageDao;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageAuthService;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManagePrintRecorService;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageReadRecordService;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageService;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageVersionService;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryBean;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryRoleAuthBean;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryRoleAuthService;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreFileManageServiceImpl implements CoreFileManageService {
   @Autowired
   private CoreFileManageDao coreFileManageDao;
   @Autowired
   @Lazy
   private CoreFileManageVersionService coreFileManageVersionService;
   @Autowired
   @Lazy
   private CoreFileManageAuthService coreFileManageAuthService;
   @Autowired
   @Lazy
   private CoreFileManageReadRecordService coreFileManageReadRecordService;
   @Autowired
   @Lazy
   private CoreFileManagePrintRecorService coreFileManagePrintRecorService;
   @Autowired
   @Lazy
   private CoreFileCategoryService coreFileCategoryService;
   @Autowired
   @Lazy
   private CoreFileCategoryRoleAuthService coreFileCategoryRoleAuthService;
   @Autowired
   private CoreFileService coreFileService;
   @Autowired
   private CoreFileManager coreFileManager;
   private static final Logger logger = LogManager.getLogger(CoreFileManageService.class);

   public CoreFileManageDao getDao() {
      return this.coreFileManageDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      List<CoreFileManageBean> coreFileManageList = this.selectList(new MapperParameter());
      CoreFileManageBean coreFileManage = jsonWrapper.parseUnique(CoreFileManageBean.class);
      coreFileManage.setId(ApplicationContextHelper.getNextIdentity());
      Long sortor = coreFileManageList.isEmpty() ? 1L : coreFileManageList.get(coreFileManageList.size() - 1).getSortor() + 1L;
      coreFileManage.setSortor(sortor);
      List<CoreFileCategoryRoleAuthBean> CoreFileCategoryRoleAuthList = this.coreFileCategoryRoleAuthService
         .selectListByFilter(SearchFilter.instance().match("fileCattegoryId", coreFileManage.getFileCategoryId()).filter(MatchPattern.EQ), new Order[0]);
      if (!CoreFileCategoryRoleAuthList.isEmpty()) {
         List<CoreFileManageAuthBean> coreFileManageAuthList = new ArrayList<>();
         CoreFileCategoryRoleAuthList.forEach(e -> {
            CoreFileManageAuthBean coreFileManageAuth = new CoreFileManageAuthBean();
            coreFileManageAuth.setId(ApplicationContextHelper.getNextIdentity());
            coreFileManageAuth.setFileManageId(coreFileManage.getId());
            coreFileManageAuth.setSortor(e.getSortor());
            coreFileManageAuth.setRoleId(e.getRoleId());
            coreFileManageAuth.setRoleName(e.getRoleName());
            coreFileManageAuth.setPrintFlag(e.getPrintFlag());
            coreFileManageAuth.setReadFlag(e.getReadFlag());
            coreFileManageAuthList.add(coreFileManageAuth);
         });
         this.coreFileManageAuthService.getDao().insert(coreFileManageAuthList);
      }

      this.getDao().insert(coreFileManage);
      return coreFileManage.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertManageVersion(Long id, RestJsonWrapperBean wrapper) {
      CoreFileManageBean coreFileManage = this.selectById(id);
      CoreFileCategoryBean coreFileCategory = this.coreFileCategoryService.selectById(coreFileManage.getFileCategoryId());
      CoreFileManageVersionBean coreFileManageVersion = wrapper.parseUnique(CoreFileManageVersionBean.class);
      coreFileManageVersion.setId(ApplicationContextHelper.getNextIdentity());
      coreFileManageVersion.setFileManageId(id);
      coreFileManageVersion.setVersionState("2");
      this.coreFileService
         .copy(
            this.coreFileCategoryService.getDao().getTable() + "$" + coreFileCategory.getId(),
            this.coreFileManageVersionService.getDao().getTable() + "$" + coreFileManageVersion.getId()
         );
      List<CoreFileBean> coreFileList = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance()
               .match("targetId", this.coreFileManageVersionService.getDao().getTable() + "$" + coreFileManageVersion.getId())
               .filter(MatchPattern.EQ),
            new Order[0]
         );
      if (!coreFileList.isEmpty()) {
         coreFileManageVersion.setRefFileId(coreFileList.get(0).getId());
         coreFileManageVersion.setRefFileName(coreFileList.get(0).getName());
      }

      coreFileManageVersion.setFileNo(coreFileManage.getFileNo());
      coreFileManageVersion.setAuditNo(coreFileManage.getAuditNo());
      this.coreFileManageVersionService.getDao().insert(coreFileManageVersion);
      return coreFileManageVersion.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteManageVersion(Long id, RestJsonWrapperBean wrapper) {
      this.coreFileManageVersionService.delete(wrapper);
   }

   @Override
   public Page<CoreFileManageAuthBean> selectManageAuthPaginationByFileManageId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreFileManageAuthService.selectPaginationByFilter(SearchFilter.instance().match("FILEMANAGEID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public List<Long> insertManageAuth(Long id, RestJsonWrapperBean wrapper) {
      List<CoreFileManageAuthBean> coreFileManageAuthList = wrapper.parse(CoreFileManageAuthBean.class);
      List<CoreFileManageAuthBean> coreFileManageAuthAllList = this.coreFileManageAuthService
         .selectListByFilter(SearchFilter.instance().match("FILEMANAGEID", id).filter(MatchPattern.EQ), new Order[0]);
      Long sortor = coreFileManageAuthAllList.isEmpty() ? 0L : coreFileManageAuthAllList.get(coreFileManageAuthAllList.size() - 1).getSortor();
      List<Long> idList = ApplicationContextHelper.getNextIdentityList(coreFileManageAuthList.size());

      for (int i = 0; i < coreFileManageAuthList.size(); i++) {
         coreFileManageAuthList.get(i).setId(idList.get(i));
         coreFileManageAuthList.get(i).setFileManageId(id);
         coreFileManageAuthList.get(i).setSortor(sortor = sortor + 1L);
      }

      this.coreFileManageAuthService.getDao().insert(coreFileManageAuthList);
      return idList;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteManageAuth(Long id, RestJsonWrapperBean wrapper) {
      this.coreFileManageAuthService.delete(wrapper);
   }

   @Override
   public Page<CoreFileManageReadRecordBean> selectManageReadRecordPaginationByFileManageId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreFileManageReadRecordService.selectPaginationByFilter(SearchFilter.instance().match("FILEMANAGEID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertManageReadRecord(Long id, RestJsonWrapperBean wrapper) {
      CoreFileManageReadRecordBean coreFileManageReadRecor = wrapper.parseUnique(CoreFileManageReadRecordBean.class);
      CoreFileManageVersionBean coreFileManageVersion = this.coreFileManageVersionService.selectById(coreFileManageReadRecor.getFileVersionId());
      coreFileManageReadRecor.setId(ApplicationContextHelper.getNextIdentity());
      coreFileManageReadRecor.setFileManageId(id);
      coreFileManageReadRecor.setFileVersionNo(coreFileManageVersion.getVersionNo());
      coreFileManageReadRecor.setReadUserId(LocalContextHelper.getLoginUserId());
      coreFileManageReadRecor.setReadUserName(LocalContextHelper.getLoginUserName());
      coreFileManageReadRecor.setReadOrgName(LocalContextHelper.getLoginOrgName());
      coreFileManageReadRecor.setReadTime(LocalDateTime.now());
      this.coreFileManageReadRecordService.getDao().insert(coreFileManageReadRecor);
      return coreFileManageReadRecor.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteManageReadRecord(Long id, RestJsonWrapperBean wrapper) {
      this.coreFileManageReadRecordService.delete(wrapper);
   }

   @Override
   public Page<CoreFileManagePrintRecorBean> selectManagePrintRecorPaginationByFileManageId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreFileManagePrintRecorService.selectPaginationByFilter(SearchFilter.instance().match("FILEMANAGEID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertManagePrintRecor(Long id, RestJsonWrapperBean wrapper) {
      CoreFileManagePrintRecorBean coreFileManagePrintRecor = wrapper.parseUnique(CoreFileManagePrintRecorBean.class);
      CoreFileManageVersionBean coreFileManageVersion = this.coreFileManageVersionService.selectById(coreFileManagePrintRecor.getFileVersionId());
      coreFileManagePrintRecor.setId(ApplicationContextHelper.getNextIdentity());
      coreFileManagePrintRecor.setFileManageId(id);
      coreFileManagePrintRecor.setFileVersionNo(coreFileManageVersion.getVersionNo());
      coreFileManagePrintRecor.setPrintUserId(LocalContextHelper.getLoginUserId());
      coreFileManagePrintRecor.setPrintUserName(LocalContextHelper.getLoginUserName());
      coreFileManagePrintRecor.setPrintOrgName(LocalContextHelper.getLoginOrgName());
      coreFileManagePrintRecor.setPrintTime(LocalDateTime.now());
      this.coreFileManagePrintRecorService.getDao().insert(coreFileManagePrintRecor);
      return coreFileManagePrintRecor.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteManagePrintRecor(Long id, RestJsonWrapperBean wrapper) {
      this.coreFileManagePrintRecorService.delete(wrapper);
   }

   @Override
   public String getFileNo(Long id, RestJsonWrapperBean wrapper) {
      CoreFileCategoryBean coreFileCategory = this.coreFileCategoryService.selectById(id);
      return ApplicationContextHelper.getNextSequence(this.getDao().getTable(), coreFileCategory.getFileCategoryNo());
   }

   @Override
   public CoreFileManageVersionBean selectVersionDetail(Long id, RestJsonWrapperBean wrapper) {
      return this.coreFileManageVersionService.selectDetail(id);
   }

   @Override
   public Page<CoreFileManageVersionBean> selectManageVersionPaginationByFileManageId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreFileManageVersionService.selectPaginationByFilter(SearchFilter.instance().match("FILEMANAGEID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_START)
   @Audit("AUDIT.PROCESS_START")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
      return this.coreFileManageVersionService.startProcess(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_WITHDRAW)
   @Audit("AUDIT.PROCESS_WITHDRAW")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> withdrawProcess(RestJsonWrapperBean wrapper) {
      return this.coreFileManageVersionService.withdrawProcess(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
      List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = this.coreFileManageVersionService.completeTask(wrapper);
      Long fileManageId = this.coreFileManageVersionService.selectById(coreBpmnInstanceStatusDTOList.get(0).getId()).getFileManageId();
      List<CoreFileManageVersionBean> updateCoreFileManageVersionList = this.coreFileManageVersionService
         .selectListByFilter(SearchFilter.instance().match("fileManageId", fileManageId).filter(MatchPattern.EQ), new Order[0]);
      coreBpmnInstanceStatusDTOList.forEach(e -> {
         if (e.getProcessStatus().equals(ProcessStatus.DONE)) {
            updateCoreFileManageVersionList.forEach(m -> {
               if (((Long)e.getId()).equals(m.getId())) {
                  m.setVersionState("1");
                  m.setEffectiveDate(LocalDate.now());
               } else if (!((Long)e.getId()).equals(m.getId()) && "1".equals(m.getVersionState())) {
                  m.setVersionState("0");
                  m.setExpiryDate(LocalDate.now());
               }
            });
         }
      });
      this.coreFileManageVersionService.getDao().update(updateCoreFileManageVersionList, "VERSIONSTATE", "EFFECTIVEDATE", "EXPIRYDATE");
      return coreBpmnInstanceStatusDTOList;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_END)
   @Audit("AUDIT.PROCESS_END")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> endTask(RestJsonWrapperBean wrapper) {
      return this.coreFileManageVersionService.endTask(wrapper);
   }

   @Override
   public CoreBpmnTaskStatusDTO selectBpmnTaskStatus(RestJsonWrapperBean wrapper) {
      return this.coreFileManageVersionService.selectBpmnTaskStatus(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_REJECT")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
      return this.coreFileManageVersionService.rejectTask(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> oddRejectTask(RestJsonWrapperBean wrapper) {
      return this.coreFileManageVersionService.oddRejectTask(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
   @Audit("AUDIT.PROCESS_UNDO")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> undo(RestJsonWrapperBean wrapper) {
      return this.coreFileManageVersionService.undo(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void batchFileLink(RestJsonWrapperBean wrapper) {
      List<Long> fileManageIdList = wrapper.parseId(Long.class);
      List<CoreFileManageVersionBean> coreFileManageVersionList = this.coreFileManageVersionService
         .selectListByFilter(SearchFilter.instance().match("FILEMANAGEID", fileManageIdList).filter(MatchPattern.OR), new Order[0]);
      List<CoreFileBean> coreFileList = new ArrayList<>();

      try {
         coreFileManageVersionList.forEach(e -> {
            BufferedInputStream in = null;
            ByteArrayOutputStream out = null;

            try {
               URL urlIn = new URL(e.getFilePath());
               String fileName = urlIn.getFile().substring(urlIn.getFile().lastIndexOf("/") + 1, urlIn.getFile().length());
               CoreFileBean coreFile = new CoreFileBean();
               coreFile.setId(ApplicationContextHelper.getNextIdentity());
               coreFile.setTargetId(this.coreFileManageVersionService.getDao().getTable() + "$" + e.getId());
               coreFile.setName(fileName);
               coreFile.setMd5Name(EncryptUtils.MD5Encrypt(coreFile.getId() + "$" + coreFile.getName()));
               coreFile.setFileExt(FileUtils.getFileExtension(coreFile.getName()));
               coreFile.setScope(FileScope.secure.name());
               coreFile.setTimes(0L);
               coreFile.setCreatedTime(LocalDateTime.now());
               coreFile.setDownloadable("1");
               String uploadFile = SystemUtils.getWorkingPath() + this.coreFileManager.getDownloadUrl(coreFile).replace("/", File.separator);
               HttpURLConnection connectionIn = (HttpURLConnection)urlIn.openConnection();
               in = new BufferedInputStream(connectionIn.getInputStream());
               out = new ByteArrayOutputStream();
               byte[] b = new byte[1024];

               int n;
               while ((n = in.read(b)) != -1) {
                  out.write(b, 0, n);
               }

               in.close();
               out.close();
               byte[] bytes = out.toByteArray();
               String uploadPath = uploadFile.substring(0, uploadFile.lastIndexOf(File.separator));
               File file = new File(uploadPath);
               if (!file.exists()) {
                  file.mkdirs();
               }

               new File(uploadPath).createNewFile();
               Path path = Paths.get(uploadFile);
               Files.write(path, bytes);
               coreFile.setSize((long)bytes.length);
               coreFileList.add(coreFile);
               e.setRefFileId(coreFile.getId());
               return;
            } catch (Exception var28) {
               logger.error(var28.getMessage(), (Throwable)var28);
            } finally {
               if (null != in) {
                  try {
                     in.close();
                  } catch (IOException var27) {
                     var27.printStackTrace();
                  }
               }

               if (null != out) {
                  try {
                     out.close();
                  } catch (IOException var26) {
                     var26.printStackTrace();
                  }
               }
            }
         });
      } catch (Exception var6) {
         logger.error(var6.getMessage(), (Throwable)var6);
         return;
      }

      this.coreFileService.getDao().insert(coreFileList);
      this.coreFileManageVersionService.getDao().update(coreFileManageVersionList, "REFFILEID");
   }

   @Override
   public Page<CoreFileManageBean> selectRawPagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(parameter, rowBounds);
   }

   @Override
   public Page<CoreFileManageBean> selectAuditablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Class<CoreFileManageVersionBean> type = this.coreFileManageVersionService.getDao().getType();
      if (Auditable.class.isAssignableFrom(type)) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE);
      }

      parameter.setAuditAuthority();
      parameter.setOrgAuthority();
      parameter.setAuditableQueries();
      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      List<CoreFileManageVersionBean> coreFileManageVersionList = this.coreFileManageVersionService.selectList(parameter);
      List<Long> fileManageIdList = coreFileManageVersionList.stream().map(e -> e.getFileManageId()).collect(Collectors.toList());
      MapperParameter parameter1 = new MapperParameter();
      if (!fileManageIdList.isEmpty()) {
         parameter1.setFilter(SearchFilter.instance().match("ID", fileManageIdList).filter(MatchPattern.OR));
      } else {
         parameter1.setFilter(SearchFilter.instance().match("ID", "-1").filter(MatchPattern.EQ));
      }

      return this.selectPagination(parameter1, rowBounds);
   }

   @Override
   public Page<CoreFileManageBean> selectSearchablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Class<CoreFileManageVersionBean> type = this.coreFileManageVersionService.getDao().getType();
      if (Auditable.class.isAssignableFrom(type)) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.APPROVE_OR_DONE);
      }

      parameter.setSearchableQueries();
      BeanPropertyDescriptor createdByOrgIdPropertyDescriptor = PersistableMetadataHelper.getCreatedByOrgIdPropertyDescriptor(type);
      if (createdByOrgIdPropertyDescriptor != null) {
         parameter.setOrgAuthority();
      }

      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      List<CoreFileManageVersionBean> coreFileManageVersionList = this.coreFileManageVersionService.selectList(parameter);
      List<Long> fileManageIdList = coreFileManageVersionList.stream().map(e -> e.getFileManageId()).collect(Collectors.toList());
      MapperParameter parameter1 = new MapperParameter();
      if (!fileManageIdList.isEmpty()) {
         parameter1.setFilter(SearchFilter.instance().match("ID", fileManageIdList).filter(MatchPattern.OR));
      } else {
         parameter1.setFilter(SearchFilter.instance().match("ID", "-1").filter(MatchPattern.EQ));
      }

      return this.selectPagination(parameter1, rowBounds);
   }
}
