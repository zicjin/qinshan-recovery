package com.sunwayworld.cloud.module.lcdp.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.sunwayworld.cloud.module.lcdp.base.LcdpResultDTO;
import com.sunwayworld.cloud.module.lcdp.base.annotation.Mapping;
import com.sunwayworld.cloud.module.lcdp.base.annotation.MappingType;
import com.sunwayworld.cloud.module.lcdp.base.service.LcdpBaseService;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeMenuBean;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeMenuService;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.service.LcdpCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalConfigBean;
import com.sunwayworld.cloud.module.lcdp.config.service.LcdpGlobalConfigService;
import com.sunwayworld.cloud.module.lcdp.importrecord.bean.LcdpResourceImportRecordBean;
import com.sunwayworld.cloud.module.lcdp.importrecord.service.LcdpResourceImportRecordService;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpModuleTmplService;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.service.LcdpPageTmplService;
import com.sunwayworld.cloud.module.lcdp.resource.LcdpResourceCache;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpAnalysisResultDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpExportLogFileDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpJavaStructureDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageI18nBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpPageI18nCodeBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBacthCheckoutDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCheckoutConfigDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCompareDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceExportDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpServerScriptMethodBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpTreeAuxiliaryDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpWorkspaceDTO;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpResourceDao;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageCompService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageI18nService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpPageI18nCodeService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceHistoryService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpServerScriptMethodService;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpMapperUtils;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpPageCompConfigAnalyseHelper;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpScriptUtils;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpScriptPath;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.bean.LcdpResourceDocumentBean;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.support.LuceneIndexManager;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.support.LuceneSearchManager;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.resourceversion.service.LcdpResourceVersionService;
import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.service.LcdpSubmitLogService;
import com.sunwayworld.cloud.module.lcdp.support.LcdpScriptTemplateHelper;
import com.sunwayworld.cloud.module.lcdp.support.constant.LcdpConstant;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.LcdpTableUtils;
import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpDatabaseDao;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpDatabaseService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableFieldService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableIndexService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpViewService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.CompileException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.jdk.hints.core.CodeHintsManager;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.jdk.tools.LoadMultipleResult;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.ArchiveUtils;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.ReflectionUtils.MethodWrapper;
import com.sunwayworld.module.admin.config.bean.CoreAdminSelectConfigBean;
import com.sunwayworld.module.admin.config.service.CoreAdminSelectConfigService;
import com.sunwayworld.module.admin.request.bean.CoreRequestUrlBean;
import com.sunwayworld.module.admin.request.service.CoreRequestUrlService;
import com.sunwayworld.module.design.studio.bean.CoreStudioAdvSearchConfigBean;
import com.sunwayworld.module.design.studio.service.CoreStudioAdvSearchConfigService;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;
import com.sunwayworld.module.sys.code.service.CoreCodeCategoryService;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.page.bean.CorePageConfigDTO;
import com.sunwayworld.module.sys.page.service.CorePageConfigService;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.windup.decompiler.api.DecompilationResult;
import org.jboss.windup.decompiler.fernflower.FernflowerDecompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpResourceServiceImpl implements LcdpResourceService {
   private static final Logger log = LoggerFactory.getLogger(LcdpResourceServiceImpl.class);
   @Autowired
   private LcdpResourceDao lcdpResourceDao;
   @Autowired
   private LcdpResourceHistoryService lcdpResourceHistoryService;
   @Autowired
   private LcdpModulePageCompService lcdpModulePageCompService;
   @Autowired
   @Lazy
   private LcdpModulePageI18nService lcdpModulePageI18nService;
   @Autowired
   private LcdpPageI18nCodeService lcdpPageI18nCodeService;
   @Autowired
   private CoreI18nMessageService coreI18nMessageService;
   @Autowired
   private CoreI18nService coreI18nService;
   @Autowired
   private LcdpResourceLockService lcdpResourceLockService;
   @Autowired
   private LcdpTableService lcdpTableService;
   @Autowired
   private LcdpViewService lcdpViewService;
   @Autowired
   private LcdpDatabaseService lcdpDatabaseService;
   @Autowired
   private LcdpServerScriptMethodService serverScriptMethodService;
   @Autowired
   private CoreFileManager fileManager;
   @Autowired
   private CoreFileService coreFileService;
   @Autowired
   @Lazy
   private LcdpResourceVersionService lcdpResourceVersionService;
   @Autowired
   private LcdpResourceImportRecordService resourceImportRecordService;
   @Autowired
   @Lazy
   private LcdpModuleTmplService moduleTmplService;
   @Autowired
   private CoreMenuService coreMenuService;
   @Autowired
   private CoreRolePermissionService coreRolePermissionService;
   @Autowired
   private LcdpBizProcessNodeMenuService bizProcessNodeMenuService;
   @Autowired
   private CoreCodeCategoryService coreCodeCategoryService;
   @Autowired
   private CoreCodeService coreCodeService;
   @Autowired
   private LcdpTableFieldService lcdpTableFieldService;
   @Autowired
   private LcdpTableIndexService lcdpTableIndexService;
   @Lazy
   @Autowired
   private LcdpResourceService proxyInstance;
   @Lazy
   @Autowired
   private LcdpSubmitLogService submitLogService;
   @Autowired
   private Dialect dialect;
   @Autowired
   private LcdpDatabaseDao lcdpDatabaseDao;
   @Autowired
   @Lazy
   private LcdpCheckoutRecordService checkoutRecordService;
   @Autowired
   @Lazy
   private LcdpResourceCheckoutRecordService resourceCheckoutRecordService;
   @Autowired
   private ResourceLoader resourceLoader;
   @Autowired
   private LcdpPageTmplService pageTmplService;
   @Autowired
   private CoreAdminSelectConfigService coreAdminSelectConfigService;
   @Autowired
   private LcdpGlobalConfigService lcdpGlobalConfigService;
   @Autowired
   private CorePageConfigService corePageConfigService;
   @Autowired
   private CoreStudioAdvSearchConfigService coreStudioAdvSearchConfigService;
   @Autowired
   private StringRedisTemplate stringRedisTemplate;
   @Autowired
   private CoreConstantService coreConstantService;
   private String current_environment_database = LcdpConstant.PROFILE_DB_LIST
      .stream()
      .filter(profile -> ApplicationContextHelper.isProfileActivated(profile))
      .findFirst()
      .get();

   public LcdpResourceDao getDao() {
      return this.lcdpResourceDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      LcdpResourceBean resource = wrapper.parseUnique(LcdpResourceBean.class);
      resource.setId(ApplicationContextHelper.getNextIdentity());
      List<LcdpResourceBean> resourceList = new ArrayList<>();
      List<LcdpResourceHistoryBean> resourceHistoryList = new ArrayList<>();
      if (StringUtils.equals("mapper", resource.getResourceCategory())) {
         String tableName = StringUtils.isEmpty(resource.getExt$Item("tablename")) ? "" : resource.getExt$Item("tablename");
         String resourceName = this.dealResourceName(resource);
         resource.setResourceName(resourceName);
         LcdpConstant.MAPPER_TMPL_NAME_LIST.forEach(mapperName -> {
            LcdpResourceBean mapper = new LcdpResourceBean();
            mapper.setId(ApplicationContextHelper.getNextIdentity());
            mapper.setResourceName(resource.getResourceName() + mapperName);
            mapper.setResourceCategory("mapper");
            mapper.setResourceDesc(resource.getResourceDesc());
            mapper.setParentId(resource.getParentId());
            mapper.setPath(this.generateFilePath(Arrays.asList(mapper)).get(0).getPath());
            resourceList.add(mapper);
            LcdpResourceHistoryBean mapperHistory = new LcdpResourceHistoryBean();
            mapperHistory.setId(ApplicationContextHelper.getNextIdentity());
            mapperHistory.setResourceName(mapper.getResourceName());
            mapperHistory.setResourceCategory("mapper");
            mapperHistory.setResourceDesc(resource.getResourceDesc());
            mapperHistory.setPath(mapper.getPath());
            mapperHistory.setResourceId(mapper.getId());
            mapperHistory.setVersion(1L);
            mapperHistory.setSubmitFlag("no");
            mapperHistory.setEffectFlag("no");
            mapperHistory.setModifyVersion(null);
            mapperHistory.setContent(LcdpScriptTemplateHelper.generateMapper(mapper.getPath(), tableName, mapperName));
            if (LcdpScriptUtils.validateCurrentDBMybatisMapper() && !StringUtils.containsIgnoreCase(mapperName, this.current_environment_database)) {
               mapperHistory.setSubmitFlag("yes");
               mapperHistory.setEffectFlag("yes");
               mapper.setContent(mapperHistory.getContent());
               mapper.setEffectVersion(1L);
            }

            resourceHistoryList.add(mapperHistory);
            if (StringUtils.equals("no", mapperHistory.getSubmitFlag())) {
               LcdpMapperUtils.loadMapper(mapperHistory.getPath(), false, mapperHistory.getContent());
               LcdpMapperUtils.syncMapper(mapperHistory.getPath(), false, mapperHistory.getContent());
            }
         });
         this.lcdpResourceHistoryService.getDao().insert(resourceHistoryList);
         this.lockResource(resourceList.stream().filter(e -> e.getEffectVersion() == null).collect(Collectors.toList()));
      } else {
         resourceList.add(resource);
      }

      if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resource.getResourceCategory()) && !StringUtils.equals("mapper", resource.getResourceCategory())) {
         Long moduleId = resource.getParentId();
         this.generateFilePath(Arrays.asList(resource));
         LcdpResourceHistoryBean resourceHistory = new LcdpResourceHistoryBean();
         BeanUtils.copyProperties(resource, resourceHistory);
         resourceHistory.setId(ApplicationContextHelper.getNextIdentity());
         resourceHistory.setResourceId(resource.getId());
         resourceHistory.setVersion(1L);
         resourceHistory.setSubmitFlag("no");
         resourceHistory.setEffectFlag("no");
         resourceHistory.setModifyVersion("java".equals(resourceHistory.getResourceCategory()) ? 1 : null);
         if ("java".equals(resource.getResourceCategory())) {
            String tableName = resource.getExt$Item("tablename");
            String scriptType = resource.getScriptType();
            String scriptSourceType = resource.getScriptSourceType();
            if (StringUtils.equals("service", scriptType)) {
               String resourceName = this.dealResourceName(resource);
               resource.setResourceName(resourceName);
               resourceHistory.setResourceName(resourceName);
            }

            this.generateFilePath(Arrays.asList(resource));
            resourceHistory.setPath(resource.getPath());
            String content = null;
            if (StringUtils.equals("service", scriptType)) {
               LcdpConstant.MAPPER_TMPL_NAME_LIST.forEach(mapperName -> {
                  LcdpResourceBean mapper = new LcdpResourceBean();
                  mapper.setId(ApplicationContextHelper.getNextIdentity());
                  mapper.setResourceName(StringUtils.replaceLast(resource.getResourceName(), "Service", "") + mapperName);
                  mapper.setResourceCategory("mapper");
                  mapper.setResourceDesc(resource.getResourceDesc());
                  mapper.setParentId(moduleId);
                  mapper.setPath(this.generateFilePath(Arrays.asList(mapper)).get(0).getPath());
                  resourceList.add(mapper);
                  LcdpResourceHistoryBean mapperHistory = new LcdpResourceHistoryBean();
                  mapperHistory.setId(ApplicationContextHelper.getNextIdentity());
                  mapperHistory.setResourceName(mapper.getResourceName());
                  mapperHistory.setResourceCategory("mapper");
                  mapperHistory.setPath(mapper.getPath());
                  mapperHistory.setResourceId(mapper.getId());
                  mapperHistory.setResourceDesc(resource.getResourceDesc());
                  mapperHistory.setVersion(1L);
                  mapperHistory.setSubmitFlag("no");
                  mapperHistory.setEffectFlag("no");
                  mapperHistory.setModifyVersion(null);
                  mapperHistory.setContent(LcdpScriptTemplateHelper.generateMapper(mapper.getPath(), tableName, mapperName));
                  if (LcdpScriptUtils.validateCurrentDBMybatisMapper() && !StringUtils.containsIgnoreCase(mapperName, this.current_environment_database)) {
                     mapperHistory.setSubmitFlag("yes");
                     mapperHistory.setEffectFlag("yes");
                     mapper.setContent(mapperHistory.getContent());
                     mapper.setEffectVersion(1L);
                  }

                  resourceHistoryList.add(mapperHistory);
                  if (StringUtils.equals("no", mapperHistory.getSubmitFlag())) {
                     LcdpMapperUtils.loadMapper(mapperHistory.getPath(), false, mapperHistory.getContent());
                     LcdpMapperUtils.syncMapper(mapperHistory.getPath(), false, mapperHistory.getContent());
                  }
               });
               content = StringUtils.equals("table", scriptSourceType)
                  ? LcdpScriptTemplateHelper.generateJavaScript(
                     resourceHistory.getResourceName(),
                     tableName,
                     StringUtils.replaceLast(resourceHistory.getPath().replace("server.", "mapper."), "Service", "Mapper"),
                     null
                  )
                  : LcdpScriptTemplateHelper.generateViewJavaScript(
                     resourceHistory.getResourceName(),
                     tableName,
                     StringUtils.replaceLast(resourceHistory.getPath().replace("server.", "mapper."), "Service", "Mapper")
                  );
               resourceHistory.setContent(content);
            } else {
               content = LcdpScriptTemplateHelper.generateJavaScript(resourceHistory.getResourceName(), null, null, null);
               resourceHistory.setContent(content);
            }

            String classContent = this.replaceClassName(resourceHistory.getPath(), content, resourceHistory.getResourceName(), "v1", "m1");
            resourceHistory.setClassContent(classContent);
            this.insertServerScriptMethod(resourceHistory);
         }

         resourceHistoryList.add(resourceHistory);
         this.lcdpResourceHistoryService.getDao().insert(resourceHistoryList);
         this.lockResource(resourceList.stream().filter(e -> e.getEffectVersion() == null).collect(Collectors.toList()));
      }

      this.getDao().insert(resourceList);
      List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
      resourceList.stream().forEach(e -> {
         if (!StringUtils.equals("mapper", e.getResourceCategory()) || e.getEffectVersion() == null) {
            documentList.add(new LcdpResourceDocumentBean(e));
         }
      });
      resourceHistoryList.stream().forEach(e -> {
         if (!StringUtils.equals("mapper", e.getResourceCategory()) || !StringUtils.equals("yes", e.getSubmitFlag())) {
            documentList.add(new LcdpResourceDocumentBean(e));
         }
      });
      this.addCompDocument(
         resourceList.stream().filter(e -> e.getEffectVersion() == null).collect(Collectors.toList()),
         resourceHistoryList.stream().filter(e -> StringUtils.equals("no", e.getSubmitFlag())).collect(Collectors.toList()),
         documentList
      );
      LuceneIndexManager.addIndex(documentList);
      List<LcdpResourceBean> psList = resourceList.stream()
         .filter(r -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(r.getResourceCategory()) && ObjectUtils.isEmpty(r.getEffectVersion()))
         .collect(Collectors.toList());
      this.resourceCheckoutRecordService.checkoutResource(psList);
      return resourceList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertByModuleTmpl(RestJsonWrapperBean wrapper) {
      return this.moduleTmplService.generateCodeSourceMoudleTmpl(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertByPageTmpl(RestJsonWrapperBean wrapper) {
      return this.pageTmplService.insertByPageTmpl(wrapper);
   }

   @Override
   public List<LcdpResourceTreeNodeDTO> selectCheckoutTree(RestJsonWrapperBean jsonWrapper) {
      List<Long> deleteResourceIdList = jsonWrapper.parse(LcdpResourceBean.class).stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      List<LcdpResourceBean> deletedResourceList = this.selectListByIds(deleteResourceIdList);
      List<LcdpResourceBean> deleteCategoryList = deletedResourceList.stream()
         .filter(re -> StringUtils.equals("category", re.getResourceCategory()))
         .collect(Collectors.toList());
      List<LcdpResourceBean> deleteModuleList = deletedResourceList.stream()
         .filter(re -> StringUtils.equals("module", re.getResourceCategory()))
         .collect(Collectors.toList());
      List<LcdpResourceBean> deletePageAndScriptList = deletedResourceList.stream()
         .filter(re -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(re.getResourceCategory()))
         .collect(Collectors.toList());
      deletedResourceList.stream().filter(re -> re.getParentId() == null).forEach(re -> re.setParentId(1L));
      Map<Long, List<LcdpResourceBean>> parentId2ResourceMap = deletedResourceList.stream().collect(Collectors.groupingBy(LcdpResourceBean::getParentId));
      List<LcdpResourceBean> noDealModuleList = new ArrayList<>();
      parentId2ResourceMap.forEach((parentId, resourceListx) -> {
         if (deleteCategoryList.stream().anyMatch(re -> re.getId() == parentId)) {
            deleteModuleList.removeAll(resourceListx);
            noDealModuleList.addAll(resourceListx);
         }
      });
      parentId2ResourceMap.forEach((parentId, resourceListx) -> {
         if (noDealModuleList.stream().anyMatch(re -> re.getId() == parentId)) {
            deletePageAndScriptList.removeAll(resourceListx);
         }
      });
      List<Long> categoryIdList = deleteModuleList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
      List<LcdpResourceBean> moduleList = this.selectListByFilter(
         SearchFilter.instance().match("parentId", categoryIdList).filter(MatchPattern.OR).match("deleteFlag", "0").filter(MatchPattern.EQ), new Order[0]
      );
      List<LcdpResourceCheckoutRecordBean> checkoutRecordList = this.resourceCheckoutRecordService
         .selectListByFilter(SearchFilter.instance().match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ), new Order[0]);
      Map<Long, LcdpResourceCheckoutRecordBean> resourceId2CheckOutMap = checkoutRecordList.stream()
         .filter(checkoutRecord -> !StringUtils.equals("table", checkoutRecord.getResourceCategory()))
         .collect(Collectors.toMap(LcdpResourceCheckoutRecordBean::getResourceId, Function.identity(), (v1, v2) -> v2));
      List<LcdpResourceBean> resourceList = this.selectListByFilter(
         SearchFilter.instance()
            .match("PARENTID", null)
            .filter(MatchPattern.EQ)
            .match("RESOURCECATEGORY", "category")
            .filter(MatchPattern.EQ)
            .match("DELETEFLAG", "0")
            .filter(MatchPattern.EQ),
         new Order[]{Order.asc("RESOURCENAME")}
      );
      List<Long> supplementModuleIdList = deletePageAndScriptList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
      List<LcdpResourceBean> pageAndScriptList = this.selectListByFilter(
         SearchFilter.instance().match("parentId", supplementModuleIdList).filter(MatchPattern.OR).match("deleteFlag", "0").filter(MatchPattern.EQ),
         new Order[0]
      );
      pageAndScriptList = pageAndScriptList.stream().filter(ps -> !this.noReturnDataFilter(ps)).collect(Collectors.toList());
      pageAndScriptList = this.dealTreeMapper(pageAndScriptList);
      List<LcdpResourceBean> allModuleList = new ArrayList<>();
      List<LcdpResourceBean> supplementModuleList = this.selectListByIds(supplementModuleIdList);
      allModuleList.addAll(moduleList);
      allModuleList.addAll(supplementModuleList);
      allModuleList = allModuleList.stream()
         .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LcdpResourceBean::getId))), ArrayList::new));
      resourceList.addAll(allModuleList);
      List<Long> moduleIdList = moduleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      List<LcdpResourceBean> pageScriptList = this.selectListByFilter(
         SearchFilter.instance().match("parentId", moduleIdList).filter(MatchPattern.OR).match("deleteFlag", "0").filter(MatchPattern.EQ), new Order[0]
      );
      pageScriptList = pageScriptList.stream().filter(ps -> !this.noReturnDataFilter(ps)).collect(Collectors.toList());
      pageScriptList = this.dealTreeMapper(pageScriptList);
      this.dealResourceName(pageAndScriptList);
      resourceList.addAll(pageAndScriptList);
      resourceList.addAll(pageScriptList);
      resourceList = resourceList.stream()
         .sorted(Comparator.comparing(LcdpResourceBean::getResourceName, Comparator.nullsFirst(String::compareTo)))
         .collect(Collectors.toList());

      for (LcdpResourceBean resource : allModuleList) {
         this.bulidFolder(resourceList, resource);
      }

      List<String> childIdList = pageAndScriptList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
      List<LcdpResourceLockBean> lockedResourceList = this.lcdpResourceLockService
         .selectListByFilter(
            SearchFilter.instance().match("RESOURCEID", childIdList).filter(MatchPattern.OR).match("LOCKUSERID", null).filter(MatchPattern.DIFFER),
            new Order[0]
         );
      Map<String, String> resourceLockStatusMap = lockedResourceList.stream()
         .collect(Collectors.toMap(LcdpResourceLockBean::getResourceId, LcdpResourceLockBean::getLockUserId, (v1, v2) -> v1));
      resourceList.forEach(resource -> resource.setExt$Item("checkoutstatus", resourceId2CheckOutMap.get(resource.getId()) == null ? null : "1"));
      pageAndScriptList.forEach(
         resource -> {
            if (StringUtils.equals("sysAdmin", LocalContextHelper.getLoginUserId())) {
               resource.setExt$Item("checkoutstatus", resourceLockStatusMap.containsKey(resource.getId().toString()) ? "1" : "0");
            } else {
               resource.setExt$Item(
                  "checkoutstatus",
                  resourceLockStatusMap.containsKey(resource.getId().toString())
                        && StringUtils.equals(resourceLockStatusMap.get(resource.getId().toString()), LocalContextHelper.getLoginUserId())
                     ? "1"
                     : "0"
               );
            }

            resource.setExt$Item(
               "otherCheckoutstatus",
               resourceLockStatusMap.containsKey(resource.getId().toString())
                     && !StringUtils.equals(resourceLockStatusMap.get(resource.getId().toString()), LocalContextHelper.getLoginUserId())
                  ? "1"
                  : "0"
            );
         }
      );
      resourceList.stream().filter(re -> re.getParentId() != null && re.getParentId() == 1L).forEach(re -> re.setParentId(null));
      List<Long> parentIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      List<LcdpTreeAuxiliaryDTO> treeAuxiliaryDTOList = new ArrayList<>();
      if (!parentIdList.isEmpty()) {
         treeAuxiliaryDTOList = this.getDao()
            .selectTreeChildQtyByParentId(parentIdList)
            .stream()
            .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, LcdpTreeAuxiliaryDTO.class))
            .collect(Collectors.toList());
      }

      List<Long> resultList = treeAuxiliaryDTOList.stream().map(data -> data.getParentId()).collect(Collectors.toList());
      Map<Long, List<LcdpResourceBean>> parentId2ResourceList = resourceList.stream()
         .filter(resource -> null != resource.getParentId())
         .collect(Collectors.groupingBy(LcdpResourceBean::getParentId));
      TreeDescriptor<LcdpResourceBean> descriptor = new TreeDescriptor<>("id", "parentId", "resourceName", "orderNo");
      descriptor.setParseTreeNodeFunction(t -> {
         LcdpResourceTreeNodeDTO resourceDTO = LcdpResourceTreeNodeDTO.of(t);
         resourceDTO.setCheckOutFlag(t.getExt$Item("checkoutstatus"));
         resourceDTO.setOtherUserCheckOutFlag(t.getExt$Item("otherCheckoutstatus"));
         resourceDTO.setCheckoutUser(t.getCheckoutUserName());
         resourceDTO.setLeafFlag(true);
         if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(t.getResourceCategory())) {
            resourceDTO.setResourceStatus(null == t.getEffectVersion() ? "new" : "valid");
         }

         if (resultList.contains(t.getId()) || "module".equals(t.getResourceCategory())) {
            resourceDTO.setLeafFlag(false);
         }

         if (!ObjectUtils.isEmpty(parentId2ResourceList.get(t.getId()))) {
            resourceDTO.setLeafFlag(false);
         }

         return resourceDTO;
      });
      List<LcdpResourceTreeNodeDTO> resourceDTOList = TreeHelper.parseTreeNode(resourceList, descriptor, LcdpResourceTreeNodeDTO.class);
      TreeHelper.updateChildQty(resourceDTOList);
      return resourceDTOList;
   }

   @Override
   public Page<?> callTodoScript(String path, RestJsonWrapperBean jsonWrapper) {
      String result = LcdpScriptUtils.callScriptMethod(path, jsonWrapper);
      return JSON.parseObject(result, Page.class);
   }

   @Override
   public Set<String> getCodeHintsKeyWords() {
      return CodeHintsManager.getCodeHintsKeyWords();
   }

   @Override
   public Long selectPageIdByPath(RestJsonWrapperBean wrapper) {
      String resourcePath = wrapper.getParamValue("resourcePath");
      if (StringUtils.isEmpty(resourcePath)) {
         return null;
      } else {
         LcdpResourceBean view = this.selectFirstByFilter(
            SearchFilter.instance().match("path", resourcePath).filter(MatchPattern.EQ).match("deleteFlag", "0").filter(MatchPattern.EQ), new Order[0]
         );
         return view == null ? null : view.getId();
      }
   }

   @Override
   public Long selectByPath(RestJsonWrapperBean wrapper) {
      String resourcePath = wrapper.getParamValue("resourcePath");
      int lastDotIndex = resourcePath.lastIndexOf(".");
      String scriptPath = resourcePath.substring(0, lastDotIndex);
      String path = StringUtils.endsWithIgnoreCase(scriptPath, "mapper")
         ? StringUtils.replaceLast(scriptPath, "Mapper", LcdpMapperUtils.DIALECT_MAPPER_SUFFIX)
         : scriptPath;
      LcdpResourceBean resource = this.selectFirstByFilter(SearchFilter.instance().match("path", path).filter(MatchPattern.EQ), new Order[0]);
      if (ObjectUtils.isEmpty(resource)) {
         return null;
      } else {
         return resource.getEffectVersion() == null && !StringUtils.equals(LocalContextHelper.getLoginUserId(), resource.getCreatedById())
            ? null
            : resource.getId();
      }
   }

   @Override
   public List<LcdpResourceTreeNodeDTO> selectOpenedResourceTree(RestJsonWrapperBean jsonWrapper) {
      List<Long> pageAndScriptIdList = jsonWrapper.parse(LcdpResourceBean.class).stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      if (ObjectUtils.isEmpty(pageAndScriptIdList)) {
         return null;
      } else {
         List<LcdpResourceCheckoutRecordBean> checkoutRecordList = this.resourceCheckoutRecordService
            .selectListByFilter(SearchFilter.instance().match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ), new Order[0]);
         Map<Long, LcdpResourceCheckoutRecordBean> resourceId2CheckOutMap = checkoutRecordList.stream()
            .filter(checkoutRecord -> !StringUtils.equals("table", checkoutRecord.getResourceCategory()))
            .collect(Collectors.toMap(LcdpResourceCheckoutRecordBean::getResourceId, Function.identity(), (v1, v2) -> v2));
         List<LcdpResourceBean> resourceList = new ArrayList<>();
         List<LcdpResourceBean> categoryList = this.selectListByFilter(
            SearchFilter.instance()
               .match("PARENTID", null)
               .filter(MatchPattern.EQ)
               .match("RESOURCECATEGORY", "category")
               .filter(MatchPattern.EQ)
               .match("DELETEFLAG", "0")
               .filter(MatchPattern.EQ),
            new Order[]{Order.asc("RESOURCENAME")}
         );
         categoryList.forEach(r -> r.setExt$Item("checkoutstatus", resourceId2CheckOutMap.get(r.getId()) == null ? null : "1"));
         resourceList.addAll(categoryList);
         List<LcdpResourceBean> moduleList = new ArrayList<>();
         List<LcdpResourceBean> pageAndScriptList = this.selectListByFilter(
            SearchFilter.instance().match("id", pageAndScriptIdList).filter(MatchPattern.OR).match("deleteFlag", "0").filter(MatchPattern.EQ),
            new Order[]{Order.asc("RESOURCENAME")}
         );
         List<Long> moduleIdList = pageAndScriptList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
         List<LcdpResourceBean> firstModuleList = this.selectListByFilter(
            SearchFilter.instance().match("id", moduleIdList).filter(MatchPattern.OR).match("deleteFlag", "0").filter(MatchPattern.EQ),
            new Order[]{Order.asc("RESOURCENAME")}
         );
         List<Long> categoryIdList = firstModuleList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
         List<LcdpResourceBean> otherModuleList = this.selectListByFilter(
            SearchFilter.instance().match("parentId", categoryIdList).filter(MatchPattern.OR).match("deleteFlag", "0").filter(MatchPattern.EQ),
            new Order[]{Order.asc("RESOURCENAME")}
         );
         moduleList.addAll(firstModuleList);
         moduleList.addAll(otherModuleList);
         moduleList = moduleList.stream()
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LcdpResourceBean::getId))), ArrayList::new));
         List<LcdpResourceBean> reallyPageAndScriptList = this.selectListByFilter(
               SearchFilter.instance().match("parentId", moduleIdList).filter(MatchPattern.OR).match("deleteFlag", "0").filter(MatchPattern.EQ),
               new Order[]{Order.asc("RESOURCENAME")}
            )
            .stream()
            .filter(r -> !this.noReturnDataFilter(r))
            .collect(Collectors.toList());
         reallyPageAndScriptList = this.dealTreeMapper(reallyPageAndScriptList);
         List<Long> childIdList = reallyPageAndScriptList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceLockBean> lockedResourceList = this.lcdpResourceLockService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("LOCKUSERID", null)
                  .filter(MatchPattern.DIFFER)
                  .match("RESOURCEID", childIdList.stream().map(String::valueOf).collect(Collectors.toList()))
                  .filter(MatchPattern.OR),
               new Order[0]
            );
         Map<String, String> resourceLockStatusMap = lockedResourceList.stream()
            .collect(Collectors.toMap(LcdpResourceLockBean::getResourceId, LcdpResourceLockBean::getLockUserId, (v1, v2) -> v1));
         List<Long> parentIdList = categoryList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         parentIdList.addAll(moduleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList()));
         if (ObjectUtils.isEmpty(parentIdList)) {
            return null;
         } else {
            List<LcdpTreeAuxiliaryDTO> treeAuxiliaryDTOList = this.getDao()
               .selectTreeChildQtyByParentId(parentIdList)
               .stream()
               .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, LcdpTreeAuxiliaryDTO.class))
               .collect(Collectors.toList());
            List<Long> resultList = treeAuxiliaryDTOList.stream().map(data -> data.getParentId()).collect(Collectors.toList());
            moduleList.forEach(resource -> resource.setExt$Item("checkoutstatus", resourceId2CheckOutMap.get(resource.getId()) == null ? null : "1"));
            moduleList = moduleList.stream().sorted(Comparator.comparing(LcdpResourceBean::getResourceName)).collect(Collectors.toList());
            resourceList.addAll(moduleList);

            for (LcdpResourceBean module : firstModuleList) {
               this.bulidFolder(reallyPageAndScriptList, module);
            }

            reallyPageAndScriptList.forEach(
               resource -> {
                  if (StringUtils.equals("sysAdmin", LocalContextHelper.getLoginUserId())) {
                     resource.setExt$Item("checkoutstatus", resourceLockStatusMap.containsKey(resource.getId().toString()) ? "1" : "0");
                  } else {
                     resource.setExt$Item(
                        "checkoutstatus",
                        resourceLockStatusMap.containsKey(resource.getId().toString())
                              && StringUtils.equals(resourceLockStatusMap.get(resource.getId().toString()), LocalContextHelper.getLoginUserId())
                           ? "1"
                           : "0"
                     );
                  }
               }
            );
            resourceList.addAll(reallyPageAndScriptList);
            this.dealResourceName(resourceList);
            TreeDescriptor<LcdpResourceBean> descriptor = new TreeDescriptor<>("id", "parentId", "resourceName", "orderNo");
            Map<Long, List<LcdpResourceBean>> parentId2ResourceList = resourceList.stream()
               .filter(resource -> null != resource.getParentId())
               .collect(Collectors.groupingBy(LcdpResourceBean::getParentId));
            descriptor.setParseTreeNodeFunction(
               t -> {
                  LcdpResourceTreeNodeDTO resourceDTO = LcdpResourceTreeNodeDTO.of(t);
                  if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(t.getResourceCategory())) {
                     resourceDTO.setCheckoutTime(resourceId2CheckOutMap.get(t.getId()) == null ? null : resourceId2CheckOutMap.get(t.getId()).getCheckoutTime());
                  }

                  resourceDTO.setCheckOutFlag(
                     t.getExt$Item("checkoutstatus") != null
                        ? t.getExt$Item("checkoutstatus")
                        : (
                           resourceLockStatusMap.containsKey(resourceDTO.getId())
                                 && StringUtils.equals(resourceLockStatusMap.get(resourceDTO.getId()), LocalContextHelper.getLoginUserId())
                              ? "1"
                              : "0"
                        )
                  );
                  resourceDTO.setOtherUserCheckOutFlag(
                     resourceLockStatusMap.containsKey(resourceDTO.getId())
                           && !StringUtils.equals(resourceLockStatusMap.get(resourceDTO.getId()), LocalContextHelper.getLoginUserId())
                        ? "1"
                        : "0"
                  );
                  resourceDTO.setLeafFlag(true);
                  if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(t.getResourceCategory())) {
                     resourceDTO.setResourceStatus(null == t.getEffectVersion() ? "new" : "valid");
                  }

                  if ("mapper".equals(t.getResourceCategory())) {
                     resourceDTO.setResourceStatus(null == t.getEffectVersion() ? "new" : "valid");
                  }

                  if (resultList.contains(t.getId()) || "module".equals(t.getResourceCategory())) {
                     resourceDTO.setLeafFlag(false);
                  }

                  if (!ObjectUtils.isEmpty(parentId2ResourceList.get(t.getId()))) {
                     resourceDTO.setLeafFlag(false);
                  }

                  return resourceDTO;
               }
            );
            List<LcdpResourceTreeNodeDTO> resourceDTOList = TreeHelper.parseTreeNode(resourceList, descriptor, LcdpResourceTreeNodeDTO.class);
            TreeHelper.updateChildQty(resourceDTOList);
            return resourceDTOList;
         }
      }
   }

   @Override
   public List<LcdpResourceTreeNodeDTO> selectEditTree(String parentId, RestJsonWrapperBean jsonWrapper) {
      if (StringUtils.equals(jsonWrapper.getParamValue("type"), "folder")) {
         return null;
      } else {
         List<LcdpResourceCheckoutRecordBean> checkoutRecordList = this.resourceCheckoutRecordService
            .selectListByFilter(SearchFilter.instance().match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ), new Order[0]);
         Map<Long, LcdpResourceCheckoutRecordBean> resourceId2CheckOutMap = checkoutRecordList.stream()
            .filter(checkoutRecord -> !StringUtils.equals("table", checkoutRecord.getResourceCategory()))
            .collect(Collectors.toMap(LcdpResourceCheckoutRecordBean::getResourceId, Function.identity(), (v1, v2) -> v2));
         List<LcdpResourceBean> resourceList = new ArrayList<>();
         new ArrayList();
         if ("root".equals(parentId)) {
            if (!StringUtils.isEmpty(jsonWrapper.getParamValue("resourceId"))) {
               List<LcdpResourceTreeNodeDTO> tempResourceDTOList = this.selectTreeBySkip(jsonWrapper.getParamValue("resourceId"), resourceId2CheckOutMap);
               if (tempResourceDTOList != null) {
                  return tempResourceDTOList;
               }
            }

            List<String> queryColumnList = ArrayUtils.asList("ID", "RESOURCENAME", "RESOURCEDESC", "RESOURCECATEGORY", "PARENTID", "ORDERNO");
            LcdpResourceBean condition = new LcdpResourceBean();
            condition.setParentId(null);
            condition.setResourceCategory("category");
            condition.setDeleteFlag("0");
            resourceList = this.getDao()
               .selectList(condition, ArrayUtils.asList("PARENTID", "RESOURCECATEGORY", "DELETEFLAG"), queryColumnList, new Order[]{Order.asc("RESOURCENAME")});
            resourceList.forEach(r -> r.setExt$Item("checkoutstatus", resourceId2CheckOutMap.get(r.getId()) == null ? null : "1"));
         } else {
            LcdpResourceBean parentResource = this.selectById(Long.valueOf(parentId));
            SearchFilter filter = SearchFilter.instance()
               .match("PARENTID", parentResource.getId())
               .filter(MatchPattern.EQ)
               .match("DELETEFLAG", "0")
               .filter(MatchPattern.EQ);
            if (StringUtils.equals("module", parentResource.getResourceCategory()) && !StringUtils.isEmpty(jsonWrapper.getParamValue("checkoutFlag"))) {
               filter.match("EFFECTVERSION", null).filter(MatchPattern.DIFFER);
            }

            List<LcdpResourceBean> childList = this.selectListByFilter(filter, new Order[]{Order.asc("RESOURCENAME")});
            childList = this.dealTreeMapper(childList);
            childList.forEach(r -> r.setExt$Item("checkoutstatus", resourceId2CheckOutMap.get(r.getId()) == null ? null : "1"));
            parentResource.setExt$Item("checkoutstatus", resourceId2CheckOutMap.get(parentResource.getId()) == null ? null : "1");
            resourceList.add(parentResource);
            if ("module".equals(parentResource.getResourceCategory())) {
               LcdpResourceBean pageFloder = new LcdpResourceBean();
               pageFloder.setId(Long.valueOf(parentResource.getId() + "777"));
               pageFloder.setOrderNo(1L);
               pageFloder.setResourceDesc("page");
               pageFloder.setResourceName("page");
               pageFloder.setResourceCategory("folder");
               pageFloder.setParentId(parentResource.getId());
               resourceList.add(pageFloder);
               LcdpResourceBean frontFloder = new LcdpResourceBean();
               frontFloder.setId(Long.valueOf(parentResource.getId() + "888"));
               frontFloder.setOrderNo(1L);
               frontFloder.setResourceDesc("client");
               frontFloder.setResourceName("client");
               frontFloder.setResourceCategory("folder");
               frontFloder.setParentId(parentResource.getId());
               resourceList.add(frontFloder);
               LcdpResourceBean backFloder = new LcdpResourceBean();
               backFloder.setId(Long.valueOf(parentResource.getId() + "999"));
               backFloder.setOrderNo(1L);
               backFloder.setResourceDesc("server");
               backFloder.setResourceName("server");
               backFloder.setResourceCategory("folder");
               backFloder.setParentId(parentResource.getId());
               resourceList.add(backFloder);
               LcdpResourceBean mapperFloder = new LcdpResourceBean();
               mapperFloder.setId(Long.valueOf(parentResource.getId() + "666"));
               mapperFloder.setOrderNo(1L);
               mapperFloder.setResourceDesc("mapper");
               mapperFloder.setResourceName("mapper");
               mapperFloder.setResourceCategory("folder");
               mapperFloder.setParentId(parentResource.getId());
               resourceList.add(mapperFloder);
               if (!childList.isEmpty()) {
                  List<String> childIdList = childList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
                  List<LcdpResourceLockBean> lockedResourceList = this.lcdpResourceLockService
                     .selectListByFilter(
                        SearchFilter.instance().match("RESOURCEID", childIdList).filter(MatchPattern.OR).match("LOCKUSERID", null).filter(MatchPattern.DIFFER),
                        new Order[0]
                     );
                  Map<String, String> resourceLockStatusMap = lockedResourceList.stream()
                     .collect(Collectors.toMap(LcdpResourceLockBean::getResourceId, LcdpResourceLockBean::getLockUserId, (v1, v2) -> v1));
                  if (!StringUtils.equals("sysAdmin", LocalContextHelper.getLoginUserId())) {
                     childList = childList.stream().filter(resource -> !this.noReturnDataFilter(resource)).collect(Collectors.toList());
                  }

                  childList = childList.stream()
                     .map(
                        resource -> {
                           if (StringUtils.equals("sysAdmin", LocalContextHelper.getLoginUserId())) {
                              resource.setExt$Item("checkoutstatus", resourceLockStatusMap.containsKey(resource.getId().toString()) ? "1" : "0");
                           } else {
                              resource.setExt$Item(
                                 "checkoutstatus",
                                 resourceLockStatusMap.containsKey(resource.getId().toString())
                                       && StringUtils.equals(resourceLockStatusMap.get(resource.getId().toString()), LocalContextHelper.getLoginUserId())
                                    ? "1"
                                    : "0"
                              );
                           }

                           resource.setExt$Item(
                              "otherCheckoutstatus",
                              resourceLockStatusMap.containsKey(resource.getId().toString())
                                    && !StringUtils.equals(resourceLockStatusMap.get(resource.getId().toString()), LocalContextHelper.getLoginUserId())
                                 ? "1"
                                 : "0"
                           );
                           if ("view".equals(resource.getResourceCategory())) {
                              resource.setParentId(pageFloder.getId());
                           }

                           if ("js".equals(resource.getResourceCategory())) {
                              resource.setParentId(frontFloder.getId());
                           }

                           if ("java".equals(resource.getResourceCategory())) {
                              resource.setParentId(backFloder.getId());
                           }

                           if ("mapper".equals(resource.getResourceCategory())) {
                              resource.setParentId(mapperFloder.getId());
                           }

                           return (LcdpResourceBean)resource;
                        }
                     )
                     .collect(Collectors.toList());
               }
            }

            resourceList.addAll(childList);
         }

         if (resourceList.isEmpty()) {
            return new ArrayList<>();
         } else {
            this.dealResourceName(resourceList);
            List<Long> parentIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
            List<LcdpTreeAuxiliaryDTO> treeAuxiliaryDTOList = this.getDao()
               .selectTreeChildQtyByParentId(parentIdList)
               .stream()
               .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, LcdpTreeAuxiliaryDTO.class))
               .collect(Collectors.toList());
            List<Long> resultList = treeAuxiliaryDTOList.stream().map(data -> data.getParentId()).collect(Collectors.toList());
            TreeDescriptor<LcdpResourceBean> descriptor = new TreeDescriptor<>("id", "parentId", "resourceName", "orderNo");
            Map<Long, List<LcdpResourceBean>> parentId2ResourceList = resourceList.stream()
               .filter(resource -> null != resource.getParentId())
               .collect(Collectors.groupingBy(LcdpResourceBean::getParentId));
            descriptor.setParseTreeNodeFunction(t -> {
               LcdpResourceTreeNodeDTO resourceDTO = LcdpResourceTreeNodeDTO.of(t);
               resourceDTO.setCheckOutFlag(t.getExt$Item("checkoutstatus"));
               resourceDTO.setOtherUserCheckOutFlag(t.getExt$Item("otherCheckoutstatus"));
               if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(t.getResourceCategory())) {
                  resourceDTO.setCheckoutTime(resourceId2CheckOutMap.get(t.getId()) == null ? null : resourceId2CheckOutMap.get(t.getId()).getCheckoutTime());
               }

               resourceDTO.setLeafFlag(true);
               if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(t.getResourceCategory())) {
                  resourceDTO.setResourceStatus(null == t.getEffectVersion() ? "new" : "valid");
               }

               if (resultList.contains(t.getId()) || "module".equals(t.getResourceCategory())) {
                  resourceDTO.setLeafFlag(false);
               }

               if (!ObjectUtils.isEmpty(parentId2ResourceList.get(t.getId()))) {
                  resourceDTO.setLeafFlag(false);
               }

               return resourceDTO;
            });
            List<LcdpResourceTreeNodeDTO> resourceDTOList = TreeHelper.parseTreeNode(resourceList, descriptor, LcdpResourceTreeNodeDTO.class);
            TreeHelper.updateChildQty(resourceDTOList);
            return resourceDTOList;
         }
      }
   }

   @Override
   public LcdpResourceDTO selectResourceContent(Long id, RestJsonWrapperBean wrapper) {
      List<String> queryColumnList = ArrayUtils.asList("ID", "EFFECTVERSION");
      LcdpResourceBean condition = new LcdpResourceBean();
      condition.setId(id);
      LcdpResourceBean resource = this.getDao()
         .selectFirstIfPresent(condition, ArrayUtils.asList("ID"), queryColumnList, new Order[]{Order.asc("RESOURCENAME")});
      return resource == null ? new LcdpResourceDTO() : this.getDesignResourceDTO(resource);
   }

   public LcdpResourceDTO selectScriptContent(String resourceName) {
      List<String> queryColumnList = ArrayUtils.asList("ID", "EFFECTVERSION");
      LcdpResourceBean condition = new LcdpResourceBean();
      condition.setResourceName(resourceName);
      condition.setDeleteFlag("0");
      LcdpResourceBean resource = this.getDao()
         .selectFirstIfPresent(condition, ArrayUtils.asList("RESOURCENAME", "DELETEFLAG"), queryColumnList, new Order[]{Order.asc("RESOURCENAME")});
      return resource == null ? null : this.getDesignResourceDTO(resource);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void replace(RestJsonWrapperBean wrapper) {
      String newContent = wrapper.getParamValue("newContent");
      String searchKey = wrapper.getParamValue("searchKey");
      List<LcdpResourceDocumentBean> documentList = LuceneSearchManager.searchList(wrapper);
      List<LcdpResourceDocumentBean> resourceDocList = documentList.stream().filter(f -> "1".equals(f.getType())).collect(Collectors.toList());
      List<LcdpResourceDocumentBean> historyDocList = documentList.stream().filter(f -> "0".equals(f.getType())).collect(Collectors.toList());
      List<String> resourceIdList = resourceDocList.stream().map(e -> LuceneIndexManager.getCompResourceId(e.getId())).distinct().collect(Collectors.toList());
      RestValidationResultBean validationResult = this.lcdpResourceLockService.validateResourceEditable(resourceIdList);
      if (!validationResult.getValid()) {
         throw new CheckedException(validationResult.getMessage());
      } else {
         List<LcdpResourceDocumentBean> addDocumentList = new ArrayList<>();
         List<LcdpResourceHistoryBean> updateHistoryResourceList = new ArrayList<>();
         List<LcdpModulePageCompBean> updateCompList = new ArrayList<>();
         if (!historyDocList.isEmpty()) {
            List<Long> historyResourceIdList = historyDocList.stream()
               .map(e -> Long.parseLong(LuceneIndexManager.getCompResourceId(e.getId())))
               .collect(Collectors.toList());
            List<LcdpResourceHistoryBean> historyResourceList = this.lcdpResourceHistoryService.selectListByIds(historyResourceIdList);

            for (LcdpResourceDocumentBean doc : historyDocList) {
               String docId = doc.getId();
               Long id = Long.parseLong(LuceneIndexManager.getCompResourceId(docId));
               LcdpResourceHistoryBean historyResource = historyResourceList.stream().filter(f -> ObjectUtils.equals(id, f.getId())).findFirst().orElse(null);
               if (historyResource != null) {
                  if ("comp".equals(doc.getResourceCategory())) {
                     String compId = LuceneIndexManager.getCompId(docId);
                     LcdpModulePageCompBean comp = this.lcdpModulePageCompService.selectByIdIfPresent(compId);
                     if (comp != null) {
                        String config = comp.getConfig();
                        String configExtend = comp.getConfigExtend();
                        comp.setConfig(StringUtils.replace(config, searchKey, newContent));
                        if (!StringUtils.isEmpty(configExtend)) {
                           comp.setConfigExtend(StringUtils.replace(configExtend, searchKey, newContent));
                        }

                        updateCompList.add(comp);
                        String content = comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend());
                        doc.setResourceContent(content);
                     }
                  } else {
                     String content = historyResource.getContent();
                     historyResource.setContent(StringUtils.replace(content, searchKey, newContent));
                     updateHistoryResourceList.add(historyResource);
                     doc.setResourceContent(historyResource.getContent());
                  }

                  addDocumentList.add(doc);
               }
            }
         }

         if (!resourceIdList.isEmpty()) {
            for (String resourceId : resourceIdList) {
               this.checkout(Long.parseLong(resourceId));
            }

            for (LcdpResourceDocumentBean docx : resourceDocList) {
               String docId = docx.getId();
               Long id = Long.parseLong(LuceneIndexManager.getCompResourceId(docId));
               SearchFilter filter = SearchFilter.instance()
                  .match("RESOURCEID", id)
                  .filter(MatchPattern.EQ)
                  .match("SUBMITFLAG", "no")
                  .filter(MatchPattern.SEQ)
                  .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
                  .filter(MatchPattern.SEQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ);
               LcdpResourceHistoryBean checkHistoryResource = this.lcdpResourceHistoryService.selectFirstByFilter(filter, new Order[]{Order.desc("VERSION")});
               if ("comp".equals(docx.getResourceCategory())) {
                  for (LcdpModulePageCompBean comp : this.lcdpModulePageCompService
                     .getDao()
                     .selectListByOneColumnValue(checkHistoryResource.getId(), "MODULEPAGEHISTORYID")) {
                     String config = comp.getConfig();
                     String configExtend = comp.getConfigExtend();
                     String oldContent = config + (StringUtils.isEmpty(configExtend) ? "" : configExtend);
                     if (oldContent.indexOf(searchKey) >= 0) {
                        comp.setConfig(StringUtils.replace(config, searchKey, newContent));
                        if (!StringUtils.isEmpty(configExtend)) {
                           comp.setConfigExtend(StringUtils.replace(configExtend, searchKey, newContent));
                        }

                        String compId = comp.getId();
                        updateCompList.add(comp);
                        LuceneIndexManager.deleteIndex(checkHistoryResource.getId() + "$$" + compId);
                        String content = comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend());
                        addDocumentList.add(
                           new LcdpResourceDocumentBean(
                              checkHistoryResource.getId() + "$$" + compId,
                              checkHistoryResource.getResourceName(),
                              checkHistoryResource.getPath(),
                              "comp",
                              content,
                              comp.getModulePageVersion(),
                              "0",
                              checkHistoryResource.getCreatedById()
                           )
                        );
                     }
                  }
               } else {
                  String content = checkHistoryResource.getContent();
                  checkHistoryResource.setContent(StringUtils.replace(content, searchKey, newContent));
                  updateHistoryResourceList.add(checkHistoryResource);
                  LuceneIndexManager.deleteIndex(String.valueOf(checkHistoryResource.getId()));
                  docx.setResourceContent(checkHistoryResource.getContent());
                  addDocumentList.add(new LcdpResourceDocumentBean(checkHistoryResource));
               }
            }
         }

         if (!updateCompList.isEmpty()) {
            this.lcdpModulePageCompService.getDao().update(updateCompList, "CONFIG", "CONFIGEXTEND");
         }

         List<LcdpResourceHistoryBean> updateJavaResourceList = updateHistoryResourceList.stream()
            .filter(f -> "java".equals(f.getResourceCategory()))
            .collect(Collectors.toList());
         updateJavaResourceList.stream()
            .forEach(
               resourceHistory -> {
                  Integer newModifyVersion = resourceHistory.getModifyVersion() + 1;
                  resourceHistory.setModifyVersion(newModifyVersion);
                  String classContent = this.replaceClassName(
                     resourceHistory.getPath(),
                     resourceHistory.getContent(),
                     resourceHistory.getResourceName(),
                     "v" + resourceHistory.getVersion(),
                     "m" + resourceHistory.getModifyVersion()
                  );
                  classContent = this.replaceScriptContent(classContent);
                  resourceHistory.setClassContent(classContent);
                  this.proxyInstance.saveContent(resourceHistory);
                  String className = JavaCompilerManager.getClassName(classContent);
                  Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(classContent);
                  SpringUtils.registerBean(className.toLowerCase(), clazz);
                  this.stringRedisTemplate.convertAndSend(LcdpMapperUtils.LCDP_SYNC_SCRIPT_TOPIC, classContent);
               }
            );
         List<LcdpResourceHistoryBean> updateNoJavaResourceList = updateHistoryResourceList.stream()
            .filter(f -> !"java".equals(f.getResourceCategory()))
            .collect(Collectors.toList());
         if (!updateHistoryResourceList.isEmpty()) {
            this.lcdpResourceHistoryService.getDao().update(updateNoJavaResourceList, "CONTENT");
         }

         if (!historyDocList.isEmpty()) {
            LuceneIndexManager.deleteIndex(historyDocList.stream().map(e -> e.getId()).collect(Collectors.toList()));
         }

         if (!addDocumentList.isEmpty()) {
            LuceneIndexManager.addIndex(addDocumentList);
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public LcdpResourceDTO checkout(Long id) {
      LcdpResourceDTO resourceDTO = new LcdpResourceDTO();
      LcdpResourceBean resource = this.selectById(id);
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", id).filter(MatchPattern.SEQ), new Order[]{Order.desc("VERSION")});
      LcdpResourceHistoryBean insertResourceHistory = new LcdpResourceHistoryBean();
      BeanUtils.copyProperties(resource, insertResourceHistory, LcdpConstant.COPY_IGNORE_PROPERTIES);
      insertResourceHistory.setId(ApplicationContextHelper.getNextIdentity());
      insertResourceHistory.setResourceId(resource.getId());
      insertResourceHistory.setVersion(resourceHistoryList.isEmpty() ? 1L : resourceHistoryList.get(0).getVersion() + 1L);
      insertResourceHistory.setSubmitFlag("no");
      insertResourceHistory.setEffectFlag("no");
      insertResourceHistory.setModifyVersion("java".equals(insertResourceHistory.getResourceCategory()) ? 1 : null);
      if (StringUtils.equals("java", insertResourceHistory.getResourceCategory())) {
         String content = insertResourceHistory.getClassContent();
         String classContent = this.replaceClassName(
            insertResourceHistory.getPath(), content, insertResourceHistory.getResourceName(), "v" + insertResourceHistory.getVersion(), "m1"
         );
         insertResourceHistory.setClassContent(classContent);
      }

      if ("mapper".equals(resource.getResourceCategory())) {
         LcdpMapperUtils.loadMapper(resource.getPath(), false, resource.getContent());
         LcdpMapperUtils.syncMapper(resource.getPath(), false, resource.getContent());
      }

      this.lcdpResourceHistoryService.getDao().insert(insertResourceHistory);
      LcdpResourceHistoryBean resourceHistory = resourceHistoryList.stream().filter(r -> "yes".equals(r.getEffectFlag())).findFirst().get();
      if ("view".equals(resourceHistory.getResourceCategory())) {
         resourceDTO = LcdpResourceDTO.of(resourceHistory);
         List<LcdpModulePageCompBean> modulePageCompList = this.lcdpModulePageCompService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistory.getId()).filter(MatchPattern.SEQ), new Order[0]);
         if (!modulePageCompList.isEmpty()) {
            List<LcdpModulePageCompBean> insertModulepageCompList = this.generatePageComps(modulePageCompList);
            insertModulepageCompList.forEach(source -> {
               source.setModulePageHistoryId(insertResourceHistory.getId());
               source.setModulePageVersion(insertResourceHistory.getVersion());
               EntityHelper.assignCreatedElement(source);
            });
            resourceDTO.setComponents(insertModulepageCompList);
            this.lcdpModulePageCompService.getDao().insert(insertModulepageCompList);
            List<LcdpResourceDocumentBean> documentList = new ArrayList<>();

            for (LcdpModulePageCompBean comp : insertModulepageCompList) {
               String content = comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend());
               documentList.add(
                  new LcdpResourceDocumentBean(
                     insertResourceHistory.getId() + "$$" + comp.getId(),
                     insertResourceHistory.getResourceName(),
                     insertResourceHistory.getPath(),
                     "comp",
                     content,
                     comp.getModulePageVersion(),
                     "0",
                     insertResourceHistory.getCreatedById()
                  )
               );
            }

            LuceneIndexManager.addIndex(documentList);
         }

         List<LcdpModulePageI18nBean> pageI18nList = this.lcdpModulePageI18nService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistory.getId()).filter(MatchPattern.SEQ), new Order[0]);
         if (!pageI18nList.isEmpty()) {
            pageI18nList.forEach(pageI18n -> {
               pageI18n.setId(ApplicationContextHelper.getNextIdentity());
               pageI18n.setModulePageHistoryId(insertResourceHistory.getId());
               EntityHelper.assignCreatedElement(pageI18n);
            });
            this.lcdpModulePageI18nService.getDao().insert(pageI18nList);
         }

         List<LcdpPageI18nCodeBean> pageI18nCodeList = this.lcdpPageI18nCodeService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
         if (!pageI18nCodeList.isEmpty()) {
            pageI18nCodeList.forEach(pageI18nCode -> {
               pageI18nCode.setId(ApplicationContextHelper.getNextIdentity());
               pageI18nCode.setModulePageHistoryId(insertResourceHistory.getId());
               EntityHelper.assignCreatedElement(pageI18nCode);
            });
            this.lcdpPageI18nCodeService.getDao().insert(pageI18nCodeList);
         }
      }

      List<LcdpResourceBean> resourceList = new ArrayList<>();
      resourceList.add(resource);
      this.resourceCheckoutRecordService.checkoutResource(resourceList);
      this.lcdpResourceLockService.lock(id.toString(), resourceDTO.getType());
      LuceneIndexManager.addIndex(new LcdpResourceDocumentBean(insertResourceHistory));
      return resourceDTO;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public LcdpResourceBacthCheckoutDTO batchCheckout(RestJsonWrapperBean wrapper) {
      LcdpResourceBacthCheckoutDTO result = new LcdpResourceBacthCheckoutDTO(true);
      LcdpResourceBean module = wrapper.parseUnique(this.getDao().getType());
      GikamConcurrentLocker.isolated("RESOURCE_CHECK_OUT$" + module.getId());
      List<LcdpResourceBean> queryPageAndScriptList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", module.getId()).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
      );
      queryPageAndScriptList = this.dealTreeMapper(queryPageAndScriptList);
      List<LcdpResourceBean> pageAndScriptList = queryPageAndScriptList.stream()
         .filter(resource -> !this.noReturnDataFilter(resource))
         .collect(Collectors.toList());
      List<String> pageAndScriptIdList = pageAndScriptList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
      Map<Long, LcdpResourceBean> resourceId2ResourceMap = pageAndScriptList.stream().collect(Collectors.toMap(LcdpResourceBean::getId, Function.identity()));
      List<LcdpResourceLockBean> resourceLockList = this.lcdpResourceLockService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptIdList).filter(MatchPattern.OR), new Order[0]);
      List<LcdpResourceLockBean> allowCheckLockResourceList = resourceLockList.stream()
         .filter(lock -> null == lock.getLockUserId())
         .collect(Collectors.toList());
      List<Long> toCheckoutIdList = allowCheckLockResourceList.stream()
         .map(LcdpResourceLockBean::getResourceId)
         .map(Long::valueOf)
         .collect(Collectors.toList());
      List<LcdpResourceBean> toLockResourceList = new ArrayList<>();
      StringBuffer buffer = new StringBuffer();
      AtomicBoolean flag = new AtomicBoolean(true);
      resourceLockList.forEach(lock -> {
         LcdpResourceBean resource = resourceId2ResourceMap.get(Long.valueOf(lock.getResourceId()));
         if (toCheckoutIdList.contains(Long.valueOf(lock.getResourceId()))) {
            toLockResourceList.add(resource);
         } else if (!LocalContextHelper.getLoginUserId().equals(lock.getLockUserId())) {
            flag.set(false);
            buffer.append(resource.getResourceName() + I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.RESOURCE_OTHER_CHECKOUT"));
         }
      });
      if (!flag.get()) {
         result.setValid(false);
         result.setMessage(buffer.toString());
      }

      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", toCheckoutIdList).filter(MatchPattern.OR), new Order[0]);
      Map<Long, List<LcdpResourceHistoryBean>> resourceId2ResourceHistoryMap = resourceHistoryList.stream()
         .collect(Collectors.groupingBy(LcdpResourceHistoryBean::getResourceId));
      Map<Long, Long> viewIdMap = new HashMap<>();
      Map<Long, Long> viewId2VersionMap = new HashMap<>();
      List<LcdpResourceHistoryBean> insertResourceHistoryList = new ArrayList<>();
      resourceId2ResourceHistoryMap.forEach(
         (resourceId, historyList) -> {
            LcdpResourceHistoryBean resourceHistory = historyList.stream().filter(rh -> "yes".equals(rh.getEffectFlag())).findFirst().get();
            LcdpResourceHistoryBean insertResourceHistory = new LcdpResourceHistoryBean();
            BeanUtils.copyProperties(resourceHistory, insertResourceHistory, LcdpConstant.COPY_IGNORE_PROPERTIES);
            insertResourceHistory.setId(ApplicationContextHelper.getNextIdentity());
            insertResourceHistory.setVersion(historyList.size() + 1L);
            insertResourceHistory.setSubmitFlag("no");
            insertResourceHistory.setEffectFlag("no");
            insertResourceHistory.setModifyVersion("java".equals(insertResourceHistory.getResourceCategory()) ? 1 : null);
            if (StringUtils.equals("java", insertResourceHistory.getResourceCategory())) {
               String content = insertResourceHistory.getClassContent();
               String classContent = this.replaceClassName(
                  insertResourceHistory.getPath(), content, insertResourceHistory.getResourceName(), "v" + insertResourceHistory.getVersion(), "m1"
               );
               insertResourceHistory.setClassContent(classContent);
            }

            if ("view".equals(resourceHistory.getResourceCategory())) {
               viewIdMap.put(resourceHistory.getId(), insertResourceHistory.getId());
               viewId2VersionMap.put(insertResourceHistory.getId(), insertResourceHistory.getVersion());
            }

            if ("mapper".equals(resourceHistory.getResourceCategory())) {
               LcdpMapperUtils.loadMapper(resourceHistory.getPath(), false, resourceHistory.getContent());
               LcdpMapperUtils.syncMapper(resourceHistory.getPath(), false, resourceHistory.getContent());
            }

            insertResourceHistoryList.add(insertResourceHistory);
         }
      );
      this.lcdpResourceHistoryService.getDao().insert(insertResourceHistoryList);
      if (!viewIdMap.isEmpty()) {
         List<Long> modulePageHistoryIdList = viewIdMap.keySet().stream().collect(Collectors.toList());
         List<LcdpModulePageCompBean> modulePageCompList = this.lcdpModulePageCompService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", modulePageHistoryIdList).filter(MatchPattern.OR), new Order[0]);
         if (!modulePageCompList.isEmpty()) {
            Map<Long, List<LcdpModulePageCompBean>> modulePageHistoryId2PageCompMap = modulePageCompList.stream()
               .collect(Collectors.groupingBy(LcdpModulePageCompBean::getModulePageHistoryId));
            List<LcdpModulePageCompBean> insertPageCompList = new ArrayList<>();
            modulePageHistoryId2PageCompMap.forEach((k, v) -> {
               List<LcdpModulePageCompBean> insertModulepageCompList = this.generatePageComps((List<LcdpModulePageCompBean>)v);
               insertModulepageCompList.forEach(source -> {
                  Long newModulePageHistoryId = viewIdMap.get(source.getModulePageHistoryId());
                  source.setModulePageHistoryId(newModulePageHistoryId);
                  source.setModulePageVersion(viewId2VersionMap.get(newModulePageHistoryId));
                  EntityHelper.assignCreatedElement(source);
               });
               insertPageCompList.addAll(insertModulepageCompList);
            });
            this.lcdpModulePageCompService.getDao().insert(insertPageCompList);
         }

         List<LcdpModulePageI18nBean> pageI18nList = this.lcdpModulePageI18nService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", modulePageHistoryIdList).filter(MatchPattern.OR), new Order[0]);
         if (!pageI18nList.isEmpty()) {
            pageI18nList.forEach(pageI18n -> {
               pageI18n.setId(ApplicationContextHelper.getNextIdentity());
               pageI18n.setModulePageHistoryId(viewIdMap.get(pageI18n.getModulePageHistoryId()));
               EntityHelper.assignCreatedElement(pageI18n);
            });
            this.lcdpModulePageI18nService.getDao().insert(pageI18nList);
         }

         List<LcdpPageI18nCodeBean> pageI18nCodeList = this.lcdpPageI18nCodeService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", modulePageHistoryIdList).filter(MatchPattern.OR), new Order[0]);
         if (!pageI18nCodeList.isEmpty()) {
            pageI18nCodeList.forEach(pageI18nCode -> {
               pageI18nCode.setId(ApplicationContextHelper.getNextIdentity());
               pageI18nCode.setModulePageHistoryId(viewIdMap.get(pageI18nCode.getModulePageHistoryId()));
               EntityHelper.assignCreatedElement(pageI18nCode);
            });
            this.lcdpPageI18nCodeService.getDao().insert(pageI18nCodeList);
         }
      }

      this.updateLockResource(toLockResourceList);
      this.resourceCheckoutRecordService.checkoutResource(toLockResourceList);
      List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
      insertResourceHistoryList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
      this.addCompDocument(CollectionUtils.emptyList(), insertResourceHistoryList, documentList);
      LuceneIndexManager.addIndex(documentList);
      return result;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public List<LcdpResourceBean> batchDelete(RestJsonWrapperBean wrapper) {
      List<LcdpResourceBean> resourceList = wrapper.parse(LcdpResourceBean.class);
      List<LcdpResourceBean> deleteResourceList = new ArrayList<>();
      List<LcdpResourceBean> deleteJavaScriptList = new ArrayList<>();
      List<Long> deleteResouceIdList = new ArrayList<>();
      List<LcdpResourceBean> deleteCategoryList = resourceList.stream()
         .filter(r -> StringUtils.equals("category", r.getResourceCategory()))
         .collect(Collectors.toList());
      if (!ObjectUtils.isEmpty(deleteCategoryList)) {
         List<Long> categoryIdList = deleteCategoryList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         deleteResourceList.addAll(deleteCategoryList);
         List<LcdpResourceBean> moduleList = this.selectListByFilter(
            SearchFilter.instance().match("PARENTID", categoryIdList).filter(MatchPattern.OR).match("RESOURCECATEGORY", "module").filter(MatchPattern.EQ),
            new Order[0]
         );
         if (!moduleList.isEmpty()) {
            deleteResourceList.addAll(moduleList);
            List<Long> moduleIdList = moduleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
            List<LcdpResourceBean> pageAndScriptList = this.selectListByFilter(
               SearchFilter.instance().match("PARENTID", moduleIdList).filter(MatchPattern.OR), new Order[0]
            );
            deleteResourceList.addAll(pageAndScriptList);
            List<LcdpResourceBean> javaScriptList = pageAndScriptList.stream()
               .filter(ps -> StringUtils.equals("java", ps.getResourceCategory()))
               .collect(Collectors.toList());
            deleteJavaScriptList.addAll(javaScriptList);
         }
      }

      List<LcdpResourceBean> deleteModuleList = resourceList.stream()
         .filter(r -> StringUtils.equals("module", r.getResourceCategory()))
         .collect(Collectors.toList());
      if (!ObjectUtils.isEmpty(deleteModuleList)) {
         deleteResourceList.addAll(deleteModuleList);
         List<Long> moduleIdList = deleteModuleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceBean> pageAndScriptList = this.selectListByFilter(
            SearchFilter.instance().match("PARENTID", moduleIdList).filter(MatchPattern.OR), new Order[0]
         );
         deleteResourceList.addAll(pageAndScriptList);
         List<LcdpResourceBean> javaScriptList = pageAndScriptList.stream()
            .filter(ps -> StringUtils.equals("java", ps.getResourceCategory()))
            .collect(Collectors.toList());
         deleteJavaScriptList.addAll(javaScriptList);
      }

      List<LcdpResourceBean> deletePageAndScriptList = resourceList.stream()
         .filter(r -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(r.getResourceCategory()))
         .collect(Collectors.toList());
      if (!ObjectUtils.isEmpty(deletePageAndScriptList)) {
         List<Long> pageAndScriptIdList = deletePageAndScriptList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         deletePageAndScriptList = this.selectListByIds(pageAndScriptIdList);
         deleteResourceList.addAll(deletePageAndScriptList);
         List<LcdpResourceBean> javaScriptList = deletePageAndScriptList.stream()
            .filter(ps -> StringUtils.equals("java", ps.getResourceCategory()))
            .collect(Collectors.toList());
         deleteJavaScriptList.addAll(javaScriptList);
      }

      deleteResourceList = deleteResourceList.stream()
         .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LcdpResourceBean::getId))), ArrayList::new));
      deleteJavaScriptList = deleteJavaScriptList.stream()
         .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LcdpResourceBean::getId))), ArrayList::new));
      deleteResourceList.forEach(resource -> {
         resource.setDeleteTime(LocalDateTime.now());
         resource.setDeleteFlag("1");
         resource.setDeletedById(LocalContextHelper.getLoginUserId());
         deleteResouceIdList.add(resource.getId());
      });
      this.getDao().update(deleteResourceList, new String[]{"DELETETIME", "DELETEFLAG", "DELETEDBYID"});
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", deleteResouceIdList).filter(MatchPattern.OR), new Order[0]);
      resourceHistoryList.forEach(history -> {
         history.setDeleteTime(LocalDateTime.now());
         history.setDeleteFlag("1");
         history.setDeletedById(LocalContextHelper.getLoginUserId());
      });
      this.lcdpResourceHistoryService.getDao().update(resourceHistoryList, "DELETETIME", "DELETEFLAG", "DELETEDBYID");
      List<Long> javaScriptIdList = deleteJavaScriptList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      List<LcdpServerScriptMethodBean> serverScriptMethodList = this.serverScriptMethodService
         .selectListByFilter(SearchFilter.instance().match("SERVERSCRIPTID", javaScriptIdList).filter(MatchPattern.OR), new Order[0]);
      List<Long> serverScriptMethodIdList = serverScriptMethodList.stream().map(LcdpServerScriptMethodBean::getId).collect(Collectors.toList());
      this.serverScriptMethodService.getDao().deleteByIdList(serverScriptMethodIdList);
      List<Long> viewHistoryIdList = resourceHistoryList.stream()
         .filter(history -> StringUtils.equals("view", history.getResourceCategory()))
         .map(LcdpResourceHistoryBean::getId)
         .collect(Collectors.toList());
      List<LcdpModulePageCompBean> pageCompList = this.lcdpModulePageCompService
         .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", viewHistoryIdList).filter(MatchPattern.OR), new Order[0]);
      LcdpResourceCache.evictPageI18nList(deleteResouceIdList);
      List<LcdpResourceBean> toDeletePageAndScriptList = deleteResourceList.stream()
         .filter(re -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(re.getResourceCategory()))
         .collect(Collectors.toList());
      List<String> pageAndScriptStrIdList = toDeletePageAndScriptList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
      List<LcdpResourceLockBean> resourceLockList = this.lcdpResourceLockService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptStrIdList).filter(MatchPattern.OR), new Order[0]);
      this.lcdpResourceLockService.getDao().deleteBy(resourceLockList);
      this.resourceCheckoutRecordService.unCheckoutResource(toDeletePageAndScriptList);
      deleteJavaScriptList.forEach(javaScript -> LcdpResourceCache.evict(javaScript.getPath()));
      List<String> resourceIdList = deleteResourceList.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
      LuceneIndexManager.deleteIndex(resourceIdList);
      List<String> resourceHistoryIdList = resourceHistoryList.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
      LuceneIndexManager.deleteIndex(resourceHistoryIdList);
      this.deleteCompDocumentById(resourceIdList, resourceHistoryIdList);
      List<LcdpResourceVersionBean> versionList = this.lcdpResourceVersionService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptStrIdList).filter(MatchPattern.OR), new Order[0]);
      versionList.forEach(version -> version.setResourceDeleteFlag("1"));
      LcdpSubmitLogBean submitLog = new LcdpSubmitLogBean();
      submitLog.setId(ApplicationContextHelper.getNextIdentity());
      submitLog.setCommit(I18nHelper.getMessage("LCDP.RESOURCE.DELETED"));
      List<LcdpResourceVersionBean> resourceVersionList = toDeletePageAndScriptList.stream().filter(re -> null != re.getEffectVersion()).map(re -> {
         LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
         resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
         resourceVersion.setLogId(submitLog.getId());
         resourceVersion.setResourceId(re.getId().toString());
         String[] path = re.getPath().split("\\.");
         resourceVersion.setCategoryName(path[0]);
         resourceVersion.setModuleName(path[1]);
         resourceVersion.setResourceName(re.getResourceName());
         resourceVersion.setVersion(re.getEffectVersion());
         resourceVersion.setResourcePath(re.getPath().replaceAll("\\.", "/"));
         resourceVersion.setResourceDeleteFlag("1");
         resourceVersion.setResourceAction("delete");
         resourceVersion.setResourceCategory(LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get(re.getResourceCategory()));
         return resourceVersion;
      }).collect(Collectors.toList());
      if (!resourceVersionList.isEmpty()) {
         this.submitLogService.getDao().insert(submitLog);
         this.lcdpResourceVersionService.getDao().insert(resourceVersionList);
      }

      this.lcdpResourceVersionService.getDao().update(versionList, "RESOURCEDELETEFLAG");
      return resourceList;
   }

   @Override
   public List<LcdpResourceTreeNodeDTO> selectAllCheckoutTree(RestJsonWrapperBean wrapper) {
      List<LcdpResourceCheckoutRecordBean> rcRecordList = this.resourceCheckoutRecordService.selectAll();
      if (rcRecordList.isEmpty()) {
         return new ArrayList<>();
      } else {
         List<LcdpResourceCheckoutRecordBean> categoryAndModuleList = rcRecordList.stream()
            .filter(r -> LcdpConstant.CATEGORY_MODULE_CATEGORY_LIST.contains(r.getResourceCategory()))
            .collect(Collectors.toList());
         Map<Long, String> resourceId2CheckUserMap = categoryAndModuleList.stream()
            .collect(
               Collectors.groupingBy(
                  LcdpResourceCheckoutRecordBean::getResourceId,
                  Collectors.mapping(LcdpResourceCheckoutRecordBean::getCheckoutUserName, Collectors.joining(", "))
               )
            );
         categoryAndModuleList = categoryAndModuleList.stream()
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(a -> a.getResourceId()))), ArrayList::new));
         categoryAndModuleList.forEach(cm -> cm.setCheckoutUserName(resourceId2CheckUserMap.get(cm.getResourceId())));
         List<LcdpResourceCheckoutRecordBean> checkoutPageAndScriptList = rcRecordList.stream()
            .filter(r -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(r.getResourceCategory()))
            .collect(Collectors.toList());
         List<LcdpResourceTreeNodeDTO> resourceDTOList = new ArrayList<>();
         if (!checkoutPageAndScriptList.isEmpty()) {
            categoryAndModuleList.addAll(checkoutPageAndScriptList);
            List<LcdpResourceBean> resourceList = categoryAndModuleList.stream().map(e -> {
               LcdpResourceBean resource = new LcdpResourceBean();
               BeanUtils.copyProperties(e, resource);
               resource.setId(e.getResourceId());
               resource.setOrderNo(1L);
               return resource;
            }).sorted(Comparator.comparing(LcdpResourceBean::getResourceName)).collect(Collectors.toList());
            List<LcdpResourceBean> pageAndScriptList = resourceList.stream()
               .filter(r -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(r.getResourceCategory()))
               .collect(Collectors.toList());
            Map<Long, Map<String, List<LcdpResourceBean>>> moduleId2ResourceCategoryMap = pageAndScriptList.stream()
               .collect(Collectors.groupingBy(LcdpResourceBean::getParentId, Collectors.groupingBy(LcdpResourceBean::getResourceCategory)));
            moduleId2ResourceCategoryMap.forEach((k, v) -> v.forEach((categroy, psList) -> {
               LcdpResourceBean floder = new LcdpResourceBean();
               switch (categroy) {
                  case "view":
                     floder.setId(Long.valueOf(k + "777"));
                     floder.setOrderNo(1L);
                     floder.setResourceDesc("page");
                     floder.setResourceName("page");
                     floder.setResourceCategory("folder");
                     floder.setParentId(k);
                     psList.forEach(r -> r.setParentId(floder.getId()));
                     break;
                  case "js":
                     floder.setId(Long.valueOf(k + "888"));
                     floder.setOrderNo(1L);
                     floder.setResourceDesc("client");
                     floder.setResourceName("client");
                     floder.setResourceCategory("folder");
                     floder.setParentId(k);
                     psList.forEach(r -> r.setParentId(floder.getId()));
                     break;
                  case "java":
                     floder.setId(Long.valueOf(k + "999"));
                     floder.setOrderNo(1L);
                     floder.setResourceDesc("server");
                     floder.setResourceName("server");
                     floder.setResourceCategory("folder");
                     floder.setParentId(k);
                     psList.forEach(r -> r.setParentId(floder.getId()));
                     break;
                  case "mapper":
                     floder.setId(Long.valueOf(k + "666"));
                     floder.setOrderNo(1L);
                     floder.setResourceDesc("mapper");
                     floder.setResourceName("mapper");
                     floder.setResourceCategory("folder");
                     floder.setParentId(k);
                     psList.forEach(r -> r.setParentId(floder.getId()));
               }

               resourceList.add(floder);
            }));
            TreeDescriptor<LcdpResourceBean> descriptor = new TreeDescriptor<>("id", "parentId", "resourceName", "orderNo");
            descriptor.setParseTreeNodeFunction(t -> {
               LcdpResourceTreeNodeDTO resourceDTO = LcdpResourceTreeNodeDTO.of(t);
               resourceDTO.setCheckoutUser(t.getCheckoutUserName());
               return resourceDTO;
            });
            resourceDTOList = TreeHelper.parseTreeNode(resourceList, descriptor, LcdpResourceTreeNodeDTO.class);
            TreeHelper.updateChildQty(resourceDTOList);
         }

         List<LcdpResourceCheckoutRecordBean> tableList = rcRecordList.stream()
            .filter(r -> "table".equals(r.getResourceCategory()))
            .collect(Collectors.toList());
         List<LcdpResourceCheckoutRecordBean> viewList = rcRecordList.stream()
            .filter(r -> "db-view".equals(r.getResourceCategory()))
            .collect(Collectors.toList());
         if (!tableList.isEmpty()) {
            LcdpResourceTreeNodeDTO tableRoot = new LcdpResourceTreeNodeDTO();
            tableRoot.setName("table");
            tableRoot.setDesc("table");
            tableRoot.setId("TABLES");
            tableRoot.setType("category");
            resourceDTOList.add(tableRoot);
            tableList.forEach(table -> {
               LcdpResourceTreeNodeDTO treeNodeDTO = new LcdpResourceTreeNodeDTO();
               treeNodeDTO.setName(table.getTableName());
               treeNodeDTO.setDesc(table.getTableDesc());
               treeNodeDTO.setId(table.getTableName());
               treeNodeDTO.setType("table");
               treeNodeDTO.setParentId(tableRoot.getId());
               treeNodeDTO.setCheckoutUser(table.getCheckoutUserName());
               tableRoot.addChild(treeNodeDTO);
            });
            TreeHelper.updateChildQty(resourceDTOList);
         }

         if (!viewList.isEmpty()) {
            LcdpResourceTreeNodeDTO viewRoot = new LcdpResourceTreeNodeDTO();
            viewRoot.setName("db-view");
            viewRoot.setDesc("db-view");
            viewRoot.setId("VIEWS");
            viewRoot.setType("category");
            resourceDTOList.add(viewRoot);
            viewList.forEach(view -> {
               LcdpResourceTreeNodeDTO treeNodeDTO = new LcdpResourceTreeNodeDTO();
               treeNodeDTO.setName(view.getTableName());
               treeNodeDTO.setDesc(view.getTableDesc());
               treeNodeDTO.setId(view.getTableName());
               treeNodeDTO.setType("db-view");
               treeNodeDTO.setParentId(viewRoot.getId());
               treeNodeDTO.setCheckoutUser(view.getCheckoutUserName());
               viewRoot.addChild(treeNodeDTO);
            });
            TreeHelper.updateChildQty(resourceDTOList);
         }

         return resourceDTOList;
      }
   }

   @Override
   public List<LcdpResourceDTO> selectCategorySelectableList() {
      List<LcdpResourceBean> resourceList = this.selectListByFilter(
         SearchFilter.instance().match("RESOURCECATEGORY", "category").filter(MatchPattern.SEQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ),
         new Order[]{Order.asc("RESOURCENAME")}
      );
      return resourceList.stream().map(resource -> {
         LcdpResourceDTO resourceDTO = new LcdpResourceDTO();
         resourceDTO.setId(resource.getId());
         resourceDTO.setName(resource.getResourceName());
         resourceDTO.setType(resource.getResourceCategory());
         return resourceDTO;
      }).collect(Collectors.toList());
   }

   @Override
   public List<LcdpResourceTreeNodeDTO> selectModuleTree() {
      List<LcdpResourceBean> resourceList = this.selectListByFilter(
         SearchFilter.instance()
            .match("RESOURCECATEGORY", Arrays.asList("category", "module"))
            .filter(MatchPattern.OR)
            .match("DELETEFLAG", "0")
            .filter(MatchPattern.EQ),
         new Order[]{Order.asc("RESOURCENAME")}
      );
      TreeDescriptor<LcdpResourceBean> descriptor = new TreeDescriptor<>("id", "parentId", "resourceName", "orderNo");
      descriptor.setParseTreeNodeFunction(t -> LcdpResourceTreeNodeDTO.of(t));
      List<LcdpResourceTreeNodeDTO> resourceDTOList = TreeHelper.parseTreeNode(resourceList, descriptor, LcdpResourceTreeNodeDTO.class);
      TreeHelper.updateChildQty(resourceDTOList);
      return resourceDTOList;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long copyResource(Long id, RestJsonWrapperBean wrapper) {
      Map<String, String> paramMap = wrapper.getParamMap();
      String copyName = paramMap.get("copyName");
      String desc = paramMap.get("desc");
      String parentIdStr = paramMap.get("parentId");
      Long targetParentId = Long.valueOf(parentIdStr);
      LcdpResourceBean resource = this.selectById(id);
      LcdpResourceBean target = new LcdpResourceBean();
      BeanUtils.copyProperties(resource, target, LcdpConstant.COPY_IGNORE_PROPERTIES);
      target.setId(ApplicationContextHelper.getNextIdentity());
      target.setParentId(targetParentId);
      target.setResourceName(copyName);
      target.setResourceDesc(desc);
      target.setEffectVersion(null);
      if (StringUtils.equals("java", target.getResourceCategory())) {
         String content = target.getContent();
         if (!StringUtils.isEmpty(content)) {
            content = content.replaceFirst("public\\s+class\\s+" + JavaCompilerManager.getClassName(content), "public class " + copyName);
            target.setContent(content);
         }
      }

      target.setClassContent(null);
      EntityHelper.assignCreatedElement(target);
      this.generateFilePath(Arrays.asList(target));
      this.getDao().insert(target);
      this.lockResource(Arrays.asList(target));
      this.copyHistoryResource(id, target.getId(), target.getPath(), copyName, desc);
      LuceneIndexManager.addIndex(new LcdpResourceDocumentBean(target));
      this.operateCompDocument(target, null, "add");
      return target.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long copyModule(Long moduleId, RestJsonWrapperBean wrapper) {
      Map<String, String> paramMap = wrapper.getParamMap();
      String copyName = paramMap.get("copyName");
      String desc = paramMap.get("desc");
      String parentIdStr = paramMap.get("parentId");
      String sourceModuleName = paramMap.get("sourceModuleName");
      Long targetParentId = Long.valueOf(parentIdStr);
      LcdpResourceBean source = this.selectById(moduleId);
      List<LcdpResourceBean> childrenList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", moduleId).filter(MatchPattern.SEQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
      );
      List<LcdpResourceBean> actualChildrenList = childrenList.stream().filter(child -> !this.noReturnDataFilter(child)).collect(Collectors.toList());
      LcdpResourceBean target = new LcdpResourceBean();
      BeanUtils.copyProperties(source, target, LcdpConstant.COPY_IGNORE_PROPERTIES);
      target.setId(ApplicationContextHelper.getNextIdentity());
      target.setParentId(targetParentId);
      target.setResourceName(copyName);
      target.setResourceDesc(desc);
      EntityHelper.assignCreatedElement(target);
      this.getDao().insert(target);
      if (!childrenList.isEmpty()) {
         this.copyChildren(actualChildrenList, target, sourceModuleName);
      }

      return target.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void renameModule(Long moduleId, RestJsonWrapperBean wrapper) {
      String newName = wrapper.getParamValue("newName");
      LcdpResourceBean moduleResource = this.selectById(moduleId);
      String oldModuleName = moduleResource.getResourceName();
      List<LcdpResourceBean> childrenList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", moduleId).filter(MatchPattern.SEQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
      );
      List<Long> childrenIdList = childrenList.stream().map(e -> e.getId()).collect(Collectors.toList());
      SearchFilter filter = SearchFilter.instance()
         .match("RESOURCEID", childrenIdList)
         .filter(MatchPattern.OR)
         .match("DELETEFLAG", "0")
         .filter(MatchPattern.EQ)
         .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
         .filter(MatchPattern.EQ)
         .match("SUBMITFLAG", "no")
         .filter(MatchPattern.EQ);
      List<LcdpResourceHistoryBean> childrenHistoryList = this.lcdpResourceHistoryService.selectListByFilter(filter, new Order[0]);

      for (LcdpResourceBean resource : this.dealTreeMapper(childrenList)) {
         LcdpResourceHistoryBean resourceHistory = childrenHistoryList.stream()
            .filter(f -> ObjectUtils.equals(resource.getId(), f.getResourceId()))
            .findFirst()
            .orElse(null);
         if (resourceHistory == null) {
            throw new CheckedException("模块下存在未检出文件，不可重命名!");
         }
      }

      moduleResource.setResourceName(newName);
      Map<Long, List<LcdpModulePageI18nBean>> histroyId2I18nMap = null;
      List<Long> historyIdList = childrenHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
      List<LcdpModulePageI18nBean> oldPageI18nList = this.lcdpModulePageI18nService
         .selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", historyIdList).filter(MatchPattern.OR), new Order[0]);
      if (!oldPageI18nList.isEmpty()) {
         histroyId2I18nMap = oldPageI18nList.stream().collect(Collectors.groupingBy(LcdpModulePageI18nBean::getModulePageHistoryId));
      }

      List<LcdpModulePageI18nBean> newAllPageI18nList = new ArrayList<>();
      Map<Long, List<LcdpModulePageI18nBean>> finalMap = histroyId2I18nMap;
      List<LcdpModulePageCompBean> modulePageCompList = this.lcdpModulePageCompService
         .selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", historyIdList).filter(MatchPattern.OR), new Order[0]);
      Map<Long, List<LcdpModulePageCompBean>> histroyId2ModulePageCompListMap = null;
      if (!modulePageCompList.isEmpty()) {
         histroyId2ModulePageCompListMap = modulePageCompList.stream().collect(Collectors.groupingBy(LcdpModulePageCompBean::getModulePageHistoryId));
      }

      Map<Long, List<LcdpModulePageCompBean>> finalCompMap = histroyId2ModulePageCompListMap;
      this.getDao().update(moduleResource, new String[]{"RESOURCENAME"});
      if (!childrenHistoryList.isEmpty()) {
         childrenHistoryList.stream().forEach(e -> {
            String oldPath = e.getPath();
            String newPath = oldPath.replace("." + oldModuleName + ".", "." + newName + ".");
            if ("java".equals(e.getResourceCategory())) {
               String classContent = this.replaceClassName(newPath, e.getContent(), e.getResourceName(), "v1", "m1");
               e.setClassContent(classContent);
            }

            e.setPath(newPath);
            if ("view".equals(e.getResourceCategory()) && finalMap != null) {
               List<LcdpModulePageI18nBean> lcdpModulePageI18nList = finalMap.get(e.getId());
               if (!ObjectUtils.isEmpty(lcdpModulePageI18nList)) {
                  List<LcdpModulePageI18nBean> newPageI18nList = lcdpModulePageI18nList.stream().map(i18nBean -> {
                     LcdpModulePageI18nBean newModulePageI18n = new LcdpModulePageI18nBean();
                     BeanUtils.copyProperties(i18nBean, newModulePageI18n);
                     newModulePageI18n.setId(ApplicationContextHelper.getNextIdentity());
                     newModulePageI18n.setCode(StringUtils.replace(i18nBean.getCode(), oldPath, newPath));
                     return newModulePageI18n;
                  }).collect(Collectors.toList());
                  newAllPageI18nList.addAll(newPageI18nList);
               }

               if (finalCompMap != null) {
                  List<LcdpModulePageCompBean> newModulePageCompList = finalCompMap.get(e.getId());
                  this.dealRenamePage(oldPath, newPath, newModulePageCompList);
               }
            }
         });
         this.lcdpResourceHistoryService.getDao().updateIfChanged(childrenHistoryList);
         this.lcdpModulePageI18nService.getDao().deleteBy(oldPageI18nList);
         this.lcdpModulePageI18nService.getDao().insert(newAllPageI18nList);
         this.lcdpModulePageCompService.getDao().update(modulePageCompList, "CONFIG", "CONFIGEXTEND");
         List<LcdpResourceDocumentBean> updateDocumentList = new ArrayList<>();
         childrenHistoryList.stream().forEach(e -> updateDocumentList.add(new LcdpResourceDocumentBean(e)));
         LuceneIndexManager.updateIndex(updateDocumentList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void renameResource(Long id, RestJsonWrapperBean wrapper) {
      String newName = wrapper.getParamValue("newName");
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", id).filter(MatchPattern.SEQ), new Order[0]);
      List<LcdpResourceHistoryBean> filterResourceHistoryList = resourceHistoryList.stream()
         .filter(history -> LocalContextHelper.getLoginUserId().equals(history.getCreatedById()) && "no".equals(history.getSubmitFlag()))
         .collect(Collectors.toList());
      if (!filterResourceHistoryList.isEmpty()) {
         LcdpResourceHistoryBean resourceHistory = filterResourceHistoryList.get(0);
         this.rename(Arrays.asList(resourceHistory), newName);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public Long moveResource(Long id, RestJsonWrapperBean wrapper) {
      List<LcdpResourceBean> resourceList = wrapper.parse(LcdpResourceBean.class);
      List<LcdpResourceBean> resourceUpdateList = this.generateFilePath(resourceList);
      resourceUpdateList.forEach(resource -> {
         if ("java".equals(resource.getResourceCategory())) {
            String content = resource.getContent();
            String classContent = resource.getClassContent();
            if (!StringUtils.isEmpty(content)) {
               content = content.replaceFirst("public\\s+class\\s+" + JavaCompilerManager.getClassName(content), "public class " + resource.getResourceName());
               resource.setContent(content);
            }

            if (!StringUtils.isEmpty(classContent)) {
               classContent = this.replaceClassName(resource.getPath(), classContent, resource.getResourceName(), null, null);
               resource.setClassContent(classContent);
            }
         }
      });
      this.getDao().update(resourceUpdateList, new String[]{"PARENTID", "RESOURCENAME", "RESOURCEDESC", "PATH", "CONTENT", "CLASSCONTENT"});
      Map<Long, String> resourcePathMap = resourceUpdateList.stream()
         .collect(Collectors.toMap(LcdpResourceBean::getId, LcdpResourceBean::getPath, (v1, v2) -> v2));
      List<Long> resourceIdList = resourceUpdateList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR), new Order[0]);
      LcdpResourceHistoryBean effectHistory = resourceHistoryList.stream().filter(his -> "yes".equals(his.getEffectFlag())).findFirst().get();
      LcdpResourceHistoryBean pageEffectHistory = new LcdpResourceHistoryBean();
      BeanUtils.copyProperties(effectHistory, pageEffectHistory);
      Map<String, String> pathMap = new HashMap<>();
      resourceHistoryList.forEach(his -> {
         pathMap.put(his.getPath(), resourcePathMap.get(his.getResourceId()));
         his.setPath(resourcePathMap.get(his.getResourceId()));
         his.setResourceName(resourceUpdateList.get(0).getResourceName());
         if ("java".equals(his.getResourceCategory())) {
            String content = his.getContent();
            String classContent = his.getClassContent();
            if (!StringUtils.isEmpty(content)) {
               content = content.replaceFirst("public\\s+class\\s+" + JavaCompilerManager.getClassName(content), "public class " + his.getResourceName());
               his.setContent(content);
            }

            if (!StringUtils.isEmpty(classContent)) {
               classContent = this.replaceClassName(his.getPath(), classContent, his.getResourceName(), null, null);
               his.setClassContent(classContent);
            }
         }
      });
      this.lcdpResourceHistoryService.getDao().update(resourceHistoryList, "PATH", "RESOURCENAME", "CONTENT", "CLASSCONTENT");
      LcdpResourceHistoryBean resourceHistory = resourceHistoryList.stream().filter(his -> "yes".equals(his.getEffectFlag())).findFirst().get();
      Map<Long, Long> historyIdMap = new HashMap<>();
      this.updateLockResource(resourceUpdateList);
      LcdpResourceHistoryBean insertResourceHistory = new LcdpResourceHistoryBean();
      BeanUtils.copyProperties(resourceHistory, insertResourceHistory, LcdpConstant.COPY_IGNORE_PROPERTIES);
      insertResourceHistory.setId(ApplicationContextHelper.getNextIdentity());
      insertResourceHistory.setEffectFlag("no");
      insertResourceHistory.setSubmitFlag("no");
      insertResourceHistory.setVersion(resourceHistory.getVersion() + 1L);
      EntityHelper.assignCreatedElement(insertResourceHistory);
      historyIdMap.put(resourceHistory.getId(), insertResourceHistory.getId());
      if ("java".equals(resourceHistory.getResourceCategory())) {
         String classContent = insertResourceHistory.getClassContent();
         if (!StringUtils.isEmpty(classContent)) {
            classContent = this.replaceClassName(
               insertResourceHistory.getPath(), classContent, insertResourceHistory.getResourceName(), "v" + insertResourceHistory.getVersion(), "m1"
            );
            insertResourceHistory.setClassContent(classContent);
         }

         this.insertServerScriptMethod(insertResourceHistory);
      }

      this.lcdpResourceHistoryService.getDao().insert(insertResourceHistory);
      if ("view".equals(resourceHistory.getResourceCategory())) {
         this.copyPageComps(Arrays.asList(pageEffectHistory), historyIdMap, pathMap);
      }

      List<LcdpResourceDocumentBean> updateDocumentList = new ArrayList<>();
      resourceUpdateList.stream().forEach(e -> updateDocumentList.add(new LcdpResourceDocumentBean(e)));
      resourceHistoryList.stream().forEach(e -> updateDocumentList.add(new LcdpResourceDocumentBean(e)));
      this.addCompDocument(resourceUpdateList, resourceHistoryList, updateDocumentList);
      LuceneIndexManager.updateIndex(updateDocumentList);
      LuceneIndexManager.addIndex(new LcdpResourceDocumentBean(insertResourceHistory));
      this.operateCompDocument(null, insertResourceHistory, "add");
      return resourceList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public Long moveModule(Long moduleId, RestJsonWrapperBean wrapper) {
      List<LcdpResourceBean> resourceList = wrapper.parse(LcdpResourceBean.class);
      LcdpResourceBean sourceModule = this.selectById(moduleId);
      String sourceModuleName = sourceModule.getResourceName();
      this.getDao().update(resourceList, new String[]{"RESOURCENAME", "PARENTID"});
      List<LcdpResourceBean> childrenList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", moduleId).filter(MatchPattern.SEQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
      );
      if (childrenList.isEmpty()) {
         return moduleId;
      } else {
         childrenList.forEach(child -> {
            child.setResourceName(child.getResourceName().replaceFirst(sourceModuleName, resourceList.get(0).getResourceName()));
            if ("java".equals(child.getResourceCategory())) {
               String content = child.getContent();
               String classContent = child.getClassContent();
               if (!StringUtils.isEmpty(content)) {
                  content = content.replaceFirst("public\\s+class\\s+" + JavaCompilerManager.getClassName(content), "public class " + child.getResourceName());
                  child.setContent(content);
               }

               if (!StringUtils.isEmpty(classContent)) {
                  classContent = this.replaceClassName(child.getPath(), classContent, child.getResourceName(), null, null);
                  child.setClassContent(classContent);
               }
            }
         });
         List<LcdpResourceBean> resourceUpdateList = this.generateFilePath(childrenList);
         this.getDao().update(resourceUpdateList, new String[]{"RESOURCENAME", "RESOURCEDESC", "PATH", "CLASSNAME", "CONTENT", "CLASSCONTENT"});
         Map<Long, String> resourceId2NameMap = childrenList.stream().collect(Collectors.toMap(LcdpResourceBean::getId, LcdpResourceBean::getResourceName));
         Map<Long, String> resourcePathMap = resourceUpdateList.stream()
            .collect(Collectors.toMap(LcdpResourceBean::getId, LcdpResourceBean::getPath, (v1, v2) -> v2));
         List<Long> childrenIdList = resourceUpdateList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceHistoryBean> childrenHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance().match("RESOURCEID", childrenIdList).filter(MatchPattern.OR).match("DELETEFLAG", "0").filter(MatchPattern.EQ),
               new Order[0]
            );
         Map<String, String> scriptPathMap = new HashMap<>();
         Map<String, String> reverseScriptPathMap = new HashMap<>();
         Map<String, String> pathMap = new HashMap<>();
         List<LcdpResourceHistoryBean> viewHistoryList = childrenHistoryList.stream()
            .filter(his -> "yes".equals(his.getEffectFlag()) && "view".equals(his.getResourceCategory()))
            .map(his -> {
               LcdpResourceHistoryBean resourceHistory = new LcdpResourceHistoryBean();
               BeanUtils.copyProperties(his, resourceHistory);
               return resourceHistory;
            })
            .collect(Collectors.toList());
         childrenHistoryList.forEach(his -> {
            String newPath = resourcePathMap.get(his.getResourceId());
            pathMap.put(his.getPath(), newPath);
            scriptPathMap.put(his.getPath(), newPath);
            reverseScriptPathMap.put(newPath, his.getPath());
            his.setPath(newPath);
            his.setResourceName(resourceId2NameMap.get(his.getResourceId()));
            if ("java".equals(his.getResourceCategory())) {
               String content = his.getContent();
               String classContent = his.getClassContent();
               if (!StringUtils.isEmpty(content)) {
                  content = content.replaceFirst("public\\s+class\\s+" + JavaCompilerManager.getClassName(content), "public class " + his.getResourceName());
                  his.setContent(content);
               }

               if (!StringUtils.isEmpty(classContent)) {
                  classContent = this.replaceClassName(his.getPath(), classContent, his.getResourceName(), null, null);
                  his.setClassContent(classContent);
               }
            }
         });
         this.lcdpResourceHistoryService.getDao().update(childrenHistoryList, "PATH", "RESOURCENAME", "CONTENT", "CLASSCONTENT");
         this.updateLockResource(childrenList);
         Map<Long, Long> historyIdMap = new HashMap<>();
         List<LcdpResourceHistoryBean> javaResourceHistoryList = new ArrayList<>();
         List<LcdpResourceHistoryBean> insertList = childrenHistoryList.stream()
            .filter(his -> "yes".equals(his.getEffectFlag()))
            .map(
               his -> {
                  LcdpResourceHistoryBean resourceHistory = new LcdpResourceHistoryBean();
                  BeanUtils.copyProperties(his, resourceHistory, LcdpConstant.COPY_IGNORE_PROPERTIES);
                  resourceHistory.setId(ApplicationContextHelper.getNextIdentity());
                  resourceHistory.setEffectFlag("no");
                  resourceHistory.setSubmitFlag("no");
                  resourceHistory.setVersion(his.getVersion() + 1L);
                  if (StringUtils.equals("java", resourceHistory.getResourceCategory())) {
                     resourceHistory.setContent(
                        resourceHistory.getContent().replaceFirst("public class " + his.getResourceName(), "public class " + resourceHistory.getResourceName())
                     );
                     resourceHistory.setContent(
                        resourceHistory.getContent()
                           .replace(
                              reverseScriptPathMap.get(resourceHistory.getPath()).replace("server.", "mapper.").replace("Service", "Mapper"),
                              resourceHistory.getPath().replace("server.", "mapper.").replace("Service", "Mapper")
                           )
                     );
                     String classContent = resourceHistory.getClassContent();
                     classContent = this.replaceClassName(
                           resourceHistory.getPath(), classContent, resourceHistory.getResourceName(), "v" + resourceHistory.getVersion(), "m1"
                        )
                        .replace(
                           reverseScriptPathMap.get(resourceHistory.getPath()).replace("server.", "mapper.").replace("Service", "Mapper"),
                           resourceHistory.getPath().replace("server.", "mapper.").replace("Service", "Mapper")
                        );
                     resourceHistory.setClassContent(classContent);
                     javaResourceHistoryList.add(resourceHistory);
                  }

                  if ("mapper".equals(resourceHistory.getResourceCategory())) {
                     resourceHistory.setResourceName(
                        resourceHistory.getResourceName()
                           .replaceFirst(this.firstUppercase(sourceModuleName)[1], this.firstUppercase(resourceList.get(0).getResourceName())[1])
                     );
                     String newNameSpace = null;
                     String sourceNameSpace = null;

                     for (String suffixName : LcdpConstant.MAPPER_TMPL_NAME_LIST) {
                        if (resourceHistory.getResourceName().endsWith(suffixName)) {
                           newNameSpace = StringUtils.replaceLast(resourceHistory.getPath(), suffixName, "") + "Mapper";
                           sourceNameSpace = StringUtils.replaceLast(reverseScriptPathMap.get(resourceHistory.getPath()), suffixName, "") + "Mapper";
                        }
                     }

                     resourceHistory.setContent(resourceHistory.getContent().replace(sourceNameSpace, newNameSpace));
                     LcdpMapperUtils.loadMapper(resourceHistory.getPath(), false, resourceHistory.getContent());
                     LcdpMapperUtils.syncMapper(resourceHistory.getPath(), false, resourceHistory.getContent());
                  }

                  EntityHelper.assignCreatedElement(resourceHistory);
                  historyIdMap.put(his.getId(), resourceHistory.getId());
                  return resourceHistory;
               }
            )
            .collect(Collectors.toList());
         this.insertServerScriptMethod(javaResourceHistoryList);
         insertList.stream().filter(resource -> "view".equals(resource.getResourceCategory())).forEach(resource -> scriptPathMap.forEach((k, v) -> {
            String content = resource.getContent();
            if (null != content) {
               String replaceContent = content.replaceAll(k, v);
               resource.setContent(replaceContent);
            }
         }));
         this.lcdpResourceHistoryService.getDao().insert(insertList);
         List<LcdpResourceHistoryBean> pageList = childrenHistoryList.stream()
            .filter(his -> "yes".equals(his.getEffectFlag()))
            .filter(re -> "view".equals(re.getResourceCategory()))
            .collect(Collectors.toList());
         if (!ObjectUtils.isEmpty(pageList)) {
            List<LcdpModulePageCompBean> pageCompList = this.copyPageComps(viewHistoryList, historyIdMap, pathMap);
            List<LcdpModulePageCompBean> needReplaceComp = pageCompList.stream()
               .filter(comp -> LcdpConstant.RESOURCE_COPY_COMP_TYPE_LIST.contains(comp.getType()))
               .collect(Collectors.toList());
            needReplaceComp.forEach(page -> scriptPathMap.forEach((k, v) -> {
               String config = page.getConfig() + (StringUtils.isEmpty(page.getConfigExtend()) ? "" : page.getConfigExtend());
               String replaceConfig = config.replaceAll(k, v);
               this.cutConfig(replaceConfig, page);
            }));
            this.lcdpModulePageCompService.getDao().update(needReplaceComp, "CONFIG", "CONFIGEXTEND");
         }

         List<String> indexIdList = new ArrayList<>();
         resourceUpdateList.stream().forEach(e -> indexIdList.add(String.valueOf(e.getId())));
         childrenHistoryList.stream().forEach(e -> indexIdList.add(String.valueOf(e.getId())));
         LuceneIndexManager.deleteIndex(indexIdList);
         this.deleteCompDocument(resourceUpdateList, childrenHistoryList);
         List<LcdpResourceDocumentBean> insertDocumentList = new ArrayList<>();
         insertList.stream().forEach(e -> insertDocumentList.add(new LcdpResourceDocumentBean(e)));
         this.addCompDocument(CollectionUtils.emptyList(), insertList, insertDocumentList);
         LuceneIndexManager.addIndex(insertDocumentList);
         return moduleId;
      }
   }

   @Override
   public List<LcdpResourceTreeNodeDTO> selectUncommittedTree(RestJsonWrapperBean wrapper) {
      LcdpResourceHistoryBean condition = new LcdpResourceHistoryBean();
      condition.setSubmitFlag("no");
      condition.setCreatedById(LocalContextHelper.getLoginUserId());
      condition.setDeleteFlag("0");
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService.getDao().selectList(condition, ArrayUtils.asList("RESOURCEID"));
      List<LcdpTableBean> tableList = this.lcdpTableService
         .selectListByFilter(
            SearchFilter.instance()
               .match("SUBMITFLAG", "no")
               .filter(MatchPattern.SEQ)
               .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.SEQ),
            new Order[0]
         );
      List<LcdpViewBean> viewList = this.lcdpViewService
         .selectListByFilter(
            SearchFilter.instance()
               .match("SUBMITFLAG", "no")
               .filter(MatchPattern.SEQ)
               .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.SEQ),
            new Order[0]
         );
      List<Long> resourceIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getResourceId).collect(Collectors.toList());
      return this.bulidResourceTreeNodeDTOS(resourceIdList, tableList, viewList, "uncommitQuery");
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void submitResource(List<String> resourceIdList) {
      if (!resourceIdList.isEmpty()) {
         GikamConcurrentLocker.isolated("RESOURCE_SUBMIT$" + LocalContextHelper.getLoginUserId());
         List<Long> idList = resourceIdList.stream().map(idStr -> Long.parseLong(idStr.trim())).collect(Collectors.toList());
         List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", idList).filter(MatchPattern.OR), new Order[0]);
         List<LcdpResourceBean> submitResourceList = resourceHistoryList.stream().filter(resourcex -> "no".equals(resourcex.getSubmitFlag())).map(history -> {
            LcdpResourceBean resourcex = new LcdpResourceBean();
            resourcex.setId(history.getResourceId());
            resourcex.setEffectVersion(history.getVersion());
            resourcex.setResourceCategory(history.getResourceCategory());
            resourcex.setResourceName(history.getResourceName());
            resourcex.setPath(history.getPath());
            String content = ObjectUtils.isEmpty(history.getContent()) ? "" : history.getContent();
            if ("java".equals(history.getResourceCategory())) {
               String classContent = history.getClassContent();
               classContent = this.replaceClassName(resourcex.getPath(), classContent, history.getResourceName(), "v" + history.getVersion(), null);
               resourcex.setClassContent(classContent);
               resourcex.setClassName(JavaCompilerManager.getClassName(classContent));
            }

            resourcex.setContent(content);
            return resourcex;
         }).collect(Collectors.toList());
         if (submitResourceList.isEmpty()) {
            throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.RESOURCES.TIP.CURRENT_RESOURCE_HAS_BEEN_SUBMIT"));
         } else {
            List<LcdpResourceBean> javaScriptList = submitResourceList.stream()
               .filter(sr -> StringUtils.equals("java", sr.getResourceCategory()))
               .collect(Collectors.toList());
            List<Long> javaIdList = javaScriptList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
            List<LcdpServerScriptMethodBean> serverScriptMethodList = this.serverScriptMethodService
               .selectListByFilter(SearchFilter.instance().match("SERVERSCRIPTID", javaIdList).filter(MatchPattern.OR), new Order[0]);
            serverScriptMethodList.forEach(sm -> sm.setMethodFlag("normal"));
            this.serverScriptMethodService.getDao().update(serverScriptMethodList, "METHODFLAG");
            List<LcdpResourceHistoryBean> oldList = resourceHistoryList.stream().filter(his -> "yes".equals(his.getEffectFlag())).map(his -> {
               his.setEffectFlag("no");
               return (LcdpResourceHistoryBean)his;
            }).collect(Collectors.toList());
            List<LcdpResourceHistoryBean> submitList = resourceHistoryList.stream().filter(his -> "no".equals(his.getSubmitFlag())).map(his -> {
               his.setSubmitFlag("yes");
               his.setEffectFlag("yes");
               return (LcdpResourceHistoryBean)his;
            }).collect(Collectors.toList());
            this.registerSpring(javaScriptList);

            for (LcdpResourceBean resource : submitResourceList) {
               if ("mapper".equals(resource.getResourceCategory())) {
                  LcdpMapperUtils.loadMapper(resource.getPath(), true, resource.getContent());
                  LcdpMapperUtils.syncMapper(resource.getPath(), true, resource.getContent());
                  LcdpMapperUtils.unloadMapper(resource.getPath(), false);
               }
            }

            this.getDao().update(submitResourceList, new String[]{"RESOURCENAME", "PATH", "CONTENT", "CLASSCONTENT", "EFFECTVERSION", "CLASSNAME"});
            oldList.addAll(submitList);
            this.lcdpResourceHistoryService.getDao().update(oldList, "SUBMITFLAG", "EFFECTFLAG");
            LcdpResourceCache.evictPageI18nList(idList);
            List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
            submitResourceList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
            resourceHistoryList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
            this.addCompDocument(submitResourceList, submitList, documentList);
            LuceneIndexManager.updateIndex(documentList);
            this.resourceCheckoutRecordService.unCheckoutResource(submitResourceList);
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   public void save(Long id, RestJsonWrapperBean wrapper) {
      LcdpResourceBean resource = wrapper.parseUnique(LcdpResourceBean.class);
      LcdpResourceBean queryResource = this.selectById(resource.getId());
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(
            SearchFilter.instance()
               .match("RESOURCEID", resource.getId())
               .filter(MatchPattern.EQ)
               .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.SEQ)
               .match("SUBMITFLAG", "no")
               .filter(MatchPattern.SEQ),
            new Order[0]
         );
      LcdpResourceHistoryBean resourceHistory = resourceHistoryList.stream().findFirst().get();
      Integer newModifyVersion = resourceHistory.getModifyVersion() + 1;
      resourceHistory.setContent(resource.getContent());
      resourceHistory.setModifyVersion(newModifyVersion);
      if ("java".equals(queryResource.getResourceCategory())) {
         String classContent = this.replaceClassName(
            queryResource.getPath(),
            resourceHistory.getContent(),
            resourceHistory.getResourceName(),
            "v" + resourceHistory.getVersion(),
            "m" + resourceHistory.getModifyVersion()
         );
         classContent = this.replaceScriptContent(classContent);
         resourceHistory.setClassContent(classContent);
      } else if ("mapper".equals(queryResource.getResourceCategory())) {
         LcdpMapperUtils.loadMapper(resourceHistory.getPath(), false, resource.getContent());
         LcdpMapperUtils.syncMapper(resourceHistory.getPath(), false, resource.getContent());
      }

      if (!resource.getComponents().isEmpty()) {
         this.savePageComps(resource.getId(), resource.getComponents());
      }

      if (resource.getI18n() != null) {
         this.savePageI18n(resourceHistory, resource.getI18n());
      }

      if (resource.getDependentI18n() != null) {
         this.savePageDependentI18n(resourceHistory, resource.getDependentI18n());
      }

      this.lcdpResourceHistoryService.getDao().update(resourceHistory, "CONTENT", "MODIFYVERSION", "CLASSCONTENT");
      LuceneIndexManager.updateIndex(new LcdpResourceDocumentBean(resourceHistory));
      this.operateCompDocument(null, resourceHistory, "update");
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public LcdpResultDTO saveJavaCodes(Long id, RestJsonWrapperBean wrapper) {
      LcdpResourceBean resource = wrapper.parseUnique(LcdpResourceBean.class);
      Pattern autowiredClassPattern = Pattern.compile("@Autowired\\s*(\\r|\\n)*\\s*private\\s+LcdpBaseService\\s+(?<fieldName>\\w+);");
      Matcher matcher = autowiredClassPattern.matcher(resource.getContent());
      if (matcher.find()) {
         String autowiredFieldName = matcher.group("fieldName");
         return new LcdpResultDTO("500", autowiredFieldName + "对象不能用LcdpBaseService申明");
      } else {
         List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCEID", resource.getId())
                  .filter(MatchPattern.EQ)
                  .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
                  .filter(MatchPattern.SEQ)
                  .match("SUBMITFLAG", "no")
                  .filter(MatchPattern.SEQ),
               new Order[0]
            );
         LcdpResourceHistoryBean resourceHistory = resourceHistoryList.stream().findFirst().get();
         Integer newModifyVersion = resourceHistory.getModifyVersion() + 1;
         resourceHistory.setContent(resource.getContent());
         resourceHistory.setModifyVersion(newModifyVersion);
         String classContent = this.replaceClassName(
            resourceHistory.getPath(),
            resourceHistory.getContent(),
            resourceHistory.getResourceName(),
            "v" + resourceHistory.getVersion(),
            "m" + resourceHistory.getModifyVersion()
         );
         classContent = this.replaceScriptContent(classContent);
         resourceHistory.setClassContent(classContent);
         this.proxyInstance.saveContent(resourceHistory);

         try {
            this.insertServerScriptMethod(resourceHistory);
         } catch (CompileException var11) {
            return new LcdpResultDTO("200", JSONObject.toJSON(var11.getMessage()).toString());
         }

         LuceneIndexManager.updateIndex(new LcdpResourceDocumentBean(resourceHistory));
         this.operateCompDocument(null, resourceHistory, "update");
         return new LcdpResultDTO("200", null);
      }
   }

   @Override
   public Page<LcdpResourceBean> selectPageChoosablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setChoosableQueries();
      parameter.putAll(wrapper.getExtFilter());
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> this.getDao().selectPageByCondition(parameter));
      if (page.getRows().isEmpty()) {
         return new Pagination<>(page, CollectionUtils.emptyList());
      } else {
         List<LcdpResourceBean> itemList = page.getRows()
            .parallelStream()
            .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, LcdpResourceBean.class))
            .collect(Collectors.toList());
         return new Pagination<>(page, itemList);
      }
   }

   @Override
   public Page<LcdpResourceBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setFilter(SearchFilter.instance().match("DELETEFLAG", "0").filter(MatchPattern.EQ));
      parameter.setChoosableQueries();
      parameter.putAll(wrapper.getExtFilter());
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> this.getDao().selectByCondition(parameter));
      if (page.getRows().isEmpty()) {
         return new Pagination<>(page, CollectionUtils.emptyList());
      } else {
         List<LcdpResourceBean> itemList = page.getRows()
            .parallelStream()
            .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, LcdpResourceBean.class))
            .collect(Collectors.toList());
         return new Pagination<>(page, itemList);
      }
   }

   @Override
   public List<LcdpJavaStructureDTO> selectJavaScriptStructure(RestJsonWrapperBean wrapper) {
      String idStr = wrapper.getParamValue("resourceId");
      Long resourceId = Long.valueOf(idStr);
      LcdpResourceBean resource = this.selectById(resourceId);
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resourceId).filter(MatchPattern.EQ), new Order[0]);
      LcdpResourceHistoryBean resourceHistory = resourceHistoryList.stream()
         .filter(r -> StringUtils.equals(r.getSubmitFlag(), "no") && StringUtils.equals(r.getCreatedById(), LocalContextHelper.getLoginUserId()))
         .findFirst()
         .orElse(null);
      String classContent = null;
      if (resourceHistory == null) {
         classContent = resource.getClassContent();
      } else {
         classContent = resourceHistory.getClassContent();
      }

      String className = JavaCompilerManager.getClassName(classContent);
      Class<?> javaClass = DynamicClassLoader.getClassByClassName(className);
      return this.getClassStructureInfo(javaClass, classContent);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      List<LcdpResourceBean> deleteResourceList = wrapper.parse(LcdpResourceBean.class);
      LcdpResourceBean deleteResource = deleteResourceList.get(0);
      LcdpResourceBean resource = this.selectById(deleteResource.getId());
      List<Long> javaScriptIdList = new ArrayList<>();
      List<Long> pageAndScriptIdList = new ArrayList<>();
      List<LcdpResourceBean> deleteList = new ArrayList<>();
      deleteList.add(resource);
      if (StringUtils.equals("category", resource.getResourceCategory())) {
         this.resourceCheckoutRecordService.unCheckoutResource(Arrays.asList(resource));
         List<LcdpResourceBean> moduleList = this.selectListByFilter(
            SearchFilter.instance().match("PARENTID", resource.getId()).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
         );
         if (!moduleList.isEmpty()) {
            List<Long> moduleIdList = moduleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
            List<LcdpResourceBean> pageAndScriptList = this.selectListByFilter(
               SearchFilter.instance().match("PARENTID", moduleIdList).filter(MatchPattern.OR).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
            );
            deleteList.addAll(pageAndScriptList);
            deleteList.addAll(moduleList);
            pageAndScriptList.forEach(re -> {
               pageAndScriptIdList.add(re.getId());
               if (StringUtils.equals("java", re.getResourceCategory())) {
                  javaScriptIdList.add(re.getId());
                  LcdpResourceCache.evict(re.getPath());
               }
            });
         }
      }

      if (StringUtils.equals("module", resource.getResourceCategory())) {
         this.resourceCheckoutRecordService.unCheckoutResource(Arrays.asList(resource));
         List<LcdpResourceBean> pageAndScriptList = this.selectListByFilter(
            SearchFilter.instance().match("PARENTID", resource.getId()).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
         );
         deleteList.addAll(pageAndScriptList);
         pageAndScriptList.forEach(re -> {
            pageAndScriptIdList.add(re.getId());
            if (StringUtils.equals("java", re.getResourceCategory())) {
               javaScriptIdList.add(re.getId());
               LcdpResourceCache.evict(re.getPath());
            }
         });
      }

      if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resource.getResourceCategory())) {
         this.resourceCheckoutRecordService.unCheckoutResource(Arrays.asList(resource));
         pageAndScriptIdList.add(resource.getId());
         if (StringUtils.equals("java", resource.getResourceCategory())) {
            javaScriptIdList.add(resource.getId());
            LcdpResourceCache.evict(resource.getPath());
         }
      }

      deleteList.forEach(r -> {
         r.setDeleteFlag("1");
         r.setDeletedById(LocalContextHelper.getLoginUserId());
         r.setDeleteTime(LocalDateTime.now());
      });
      this.getDao().update(deleteList, new String[]{"DELETEFLAG", "DELETEDBYID", "DELETETIME"});
      if (!javaScriptIdList.isEmpty()) {
         List<LcdpServerScriptMethodBean> serverScriptMethodList = this.serverScriptMethodService
            .selectListByFilter(SearchFilter.instance().match("SERVERSCRIPTID", javaScriptIdList).filter(MatchPattern.OR), new Order[0]);
         List<Long> serverScriptIdList = serverScriptMethodList.stream().map(LcdpServerScriptMethodBean::getId).collect(Collectors.toList());
         this.serverScriptMethodService.getDao().deleteByIdList(serverScriptIdList);
      }

      if (!pageAndScriptIdList.isEmpty()) {
         List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptIdList).filter(MatchPattern.OR), new Order[0]);
         resourceHistoryList.forEach(history -> {
            history.setDeleteFlag("1");
            history.setDeletedById(LocalContextHelper.getLoginUserId());
            history.setDeleteTime(LocalDateTime.now());
         });
         this.lcdpResourceHistoryService.getDao().update(resourceHistoryList, "DELETEFLAG", "DELETEDBYID", "DELETETIME");
         List<LcdpResourceHistoryBean> viewHistoryList = resourceHistoryList.stream()
            .filter(rh -> StringUtils.equals("view", rh.getResourceCategory()) && StringUtils.equals("yes", rh.getEffectFlag()))
            .collect(Collectors.toList());
         List<Long> viewHistoryIdList = viewHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
         List<LcdpModulePageCompBean> pageCompList = this.lcdpModulePageCompService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", viewHistoryIdList).filter(MatchPattern.OR), new Order[0]);
         LcdpResourceCache.evictPageI18nList(pageAndScriptIdList);
         List<String> indexIdList = resourceHistoryList.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
         LuceneIndexManager.deleteIndex(indexIdList);
         this.deleteCompDocumentById(CollectionUtils.emptyList(), indexIdList);
      }

      List<String> pageAndScriptStrIdList = pageAndScriptIdList.stream().map(String::valueOf).collect(Collectors.toList());
      List<LcdpResourceLockBean> resourceLockList = this.lcdpResourceLockService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptStrIdList).filter(MatchPattern.OR), new Order[0]);
      List<Long> lockIdList = resourceLockList.stream().map(LcdpResourceLockBean::getId).collect(Collectors.toList());
      this.lcdpResourceLockService.getDao().deleteByIdList(lockIdList);
      List<LcdpResourceVersionBean> versionList = this.lcdpResourceVersionService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptStrIdList).filter(MatchPattern.OR), new Order[0]);
      versionList.forEach(version -> version.setResourceDeleteFlag("1"));
      LcdpSubmitLogBean submitLog = new LcdpSubmitLogBean();
      submitLog.setId(ApplicationContextHelper.getNextIdentity());
      submitLog.setCommit(I18nHelper.getMessage("LCDP.RESOURCE.DELETED"));
      List<LcdpResourceBean> deletePageAndScriptList = this.selectListByIds(pageAndScriptIdList);
      List<LcdpResourceVersionBean> resourceVersionList = deletePageAndScriptList.stream().filter(re -> null != re.getEffectVersion()).map(re -> {
         LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
         resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
         resourceVersion.setLogId(submitLog.getId());
         resourceVersion.setResourceId(re.getId().toString());
         String[] path = re.getPath().split("\\.");
         resourceVersion.setCategoryName(path[0]);
         resourceVersion.setModuleName(path[1]);
         resourceVersion.setResourceName(re.getResourceName());
         resourceVersion.setVersion(re.getEffectVersion());
         resourceVersion.setResourcePath(re.getPath().replaceAll("\\.", "/"));
         resourceVersion.setResourceDeleteFlag("1");
         resourceVersion.setResourceAction("delete");
         resourceVersion.setResourceCategory(LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get(re.getResourceCategory()));
         return resourceVersion;
      }).collect(Collectors.toList());
      if (!resourceVersionList.isEmpty()) {
         this.submitLogService.getDao().insert(submitLog);
         this.lcdpResourceVersionService.getDao().insert(resourceVersionList);
      }

      this.lcdpResourceVersionService.getDao().update(versionList, "RESOURCEDELETEFLAG");
      List<String> indexIdList = deleteList.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
      LuceneIndexManager.deleteIndex(indexIdList);
      this.deleteCompDocumentById(indexIdList, CollectionUtils.emptyList());
   }

   @Override
   public String callScript(Object... args) {
      String scriptPath = ServletUtils.getCurrentRequest().getHeader("script-path");
      log.info("脚本调用 ==>" + scriptPath);
      if (StringUtils.isEmpty(scriptPath)) {
         throw new CheckedException("GIKAM.LCDP.SCRIPT_PATH_REQUIRED");
      }
      if (!this.validateScriptCallable(scriptPath)) {
         return "";
      } else {
         if (!LcdpScriptUtils.isDebugCall()
            && StringUtils.equalsIgnoreCase("1", ApplicationContextHelper.getConstantValue("CHECK_ACCESS_PERMISSION_AUTO_INSERT"))) {
            this.initAccessPermission(scriptPath);
         }

         if (!LcdpScriptUtils.isDebugCall()
            && ApplicationContextHelper.getEnvironment().getProperty("sunway.api.check-access-permission", boolean.class, false)) {
            this.checkAccessPermission(scriptPath);
         }

         String var3;
         try {
            var3 = LcdpScriptUtils.callScriptMethod(scriptPath, args);
         } finally {
            HttpServletResponse response = ServletUtils.getCurrentResponse();
            if (response != null) {
               String path = scriptPath.substring(0, scriptPath.lastIndexOf("."));
               response.setHeader("GIKAM-INSTANT-SAVE-PATH", path + ".updateData");
            }
         }

         return var3;
      }
   }

   @Override
   public String callScheduleScript(String path, Object... args) {
      return LcdpScriptUtils.callScriptMethod(path, args);
   }

   @Override
   public void callDownloadScript(String scriptPath, String env) {
      if (this.validateScriptCallable(scriptPath)) {
         int lastDotIndex = scriptPath.lastIndexOf(".");
         String path = scriptPath.substring(0, lastDotIndex);
         String methodName = scriptPath.substring(lastDotIndex + 1);
         if (StringUtils.equals(env, "development")) {
            LcdpScriptUtils.devCallScriptMethod(path, methodName, ArrayUtils.emptyArray(Object.class));
         } else {
            LcdpScriptUtils.proCallScriptMethod(path, methodName, ArrayUtils.emptyArray(Object.class));
         }
      }
   }

   @Transactional
   @Override
   public void manualSave(RestJsonWrapperBean wrapper) {
      String requestJson = wrapper.getRequestJson();
      Map<String, String> paramMap = wrapper.getParamMap();
      List<GenericService<?, ?>> serviceList = wrapper.getBodyServiceList();
      List<List<String>> parsedBodyList = wrapper.getBodyList();
      List<String> pathList = new ArrayList<>();
      JSONObject jo = JSONObject.parseObject(requestJson);
      int index = 0;

      while (true) {
         JSONArray array = jo.getJSONArray("b" + (index > 0 ? index : ""));
         if (array == null || array.isEmpty()) {
            int size = serviceList.size();

            for (int i = 0; i < size; i++) {
               GenericService<?, ?> service = serviceList.get(i);
               List<String> contentList = parsedBodyList.get(i);
               List<List<String>> bodyList = new ArrayList<>();
               bodyList.add(contentList);
               RestJsonWrapperBean restJsonWrapperBean = new RestJsonWrapperBean(requestJson, paramMap, bodyList, ArrayUtils.asList(service));
               if (service != null) {
                  this.instantSave(restJsonWrapperBean);
               } else {
                  LcdpScriptUtils.callScriptMethod(pathList.get(i), restJsonWrapperBean);
               }
            }

            return;
         }

         String path = array.getString(0);
         JSONArray contentArray = array.getJSONArray(1);
         if (contentArray != null && !contentArray.isEmpty()) {
            pathList.add(path);
         }

         index++;
      }
   }

   @Override
   public String getResourceClassName(String resourceName) {
      LcdpResourceBean filter = new LcdpResourceBean();
      filter.setResourceName(resourceName);
      filter.setDeleteFlag("0");
      LcdpResourceBean resource = this.getDao().selectFirstIfPresent(filter, new Order[0]);
      return resource != null ? resource.getClassName() : null;
   }

   @Override
   public String getDevResourceContent(String resourceName) {
      LcdpResourceBean filter = new LcdpResourceBean();
      filter.setResourceName(resourceName);
      filter.setDeleteFlag("0");
      LcdpResourceBean resource = this.getDao().selectFirstIfPresent(filter, new Order[0]);
      Long resourceId;
      if (resource != null) {
         resourceId = resource.getId();
      } else {
         LcdpResourceHistoryBean historyFilter = new LcdpResourceHistoryBean();
         historyFilter.setResourceName(resourceName);
         historyFilter.setDeleteFlag("0");
         historyFilter.setSubmitFlag("no");
         LcdpResourceHistoryBean resourceHistory = this.lcdpResourceHistoryService
            .getDao()
            .selectOneIfPresent(historyFilter, "resourceName", "deleteFlag", "submitFlag");
         if (resourceHistory == null) {
            return null;
         }

         resourceId = resourceHistory.getResourceId();
      }

      LcdpResourceLockBean lockFilter = new LcdpResourceLockBean();
      lockFilter.setResourceId(String.valueOf(resourceId));
      lockFilter.setLockUserId(LocalContextHelper.getLoginUserId());
      LcdpResourceLockBean lockResource = this.lcdpResourceLockService.getDao().selectFirstIfPresent(lockFilter);
      if (lockResource == null) {
         return resource.getClassContent();
      } else {
         LcdpResourceHistoryBean historyFilter = new LcdpResourceHistoryBean();
         historyFilter.setResourceId(resourceId);
         historyFilter.setDeleteFlag("0");
         LcdpResourceHistoryBean historyResource = this.lcdpResourceHistoryService.getDao().selectFirst(historyFilter, Order.desc("VERSION"));
         return historyResource.getClassContent();
      }
   }

   @Override
   public LcdpResourceCompareDTO compare(RestJsonWrapperBean wrapper) {
      String compareSource = wrapper.getParamValue("source");
      LcdpResourceCompareDTO compareDTO = new LcdpResourceCompareDTO();
      Map<String, String> paramMap = wrapper.getParamMap();
      Long id = Long.valueOf(paramMap.get("resourceId"));
      String versionStr = paramMap.get("version");
      Long version = StringUtils.isEmpty(versionStr) ? null : Long.valueOf(paramMap.get("version"));
      new ArrayList();
      new LcdpResourceHistoryBean();
      List allResourceHistoryList;
      LcdpResourceHistoryBean currentResourceHistory;
      if (StringUtils.equals("checkout", compareSource)) {
         allResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", id).filter(MatchPattern.SEQ), new Order[0]);
         currentResourceHistory = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance().match("RESOURCEID", id).filter(MatchPattern.SEQ).match("SUBMITFLAG", "no").filter(MatchPattern.SEQ), new Order[0]
            )
            .stream()
            .findFirst()
            .get();
      } else {
         currentResourceHistory = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance().match("RESOURCEID", id).filter(MatchPattern.SEQ).match("VERSION", version).filter(MatchPattern.SEQ), new Order[0]
            )
            .stream()
            .findFirst()
            .get();
         allResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance().match("RESOURCEID", id).filter(MatchPattern.SEQ).match("SUBMITFLAG", "yes").filter(MatchPattern.SEQ), new Order[0]
            );
      }

      compareDTO.setHistoryList(allResourceHistoryList);
      compareDTO.setCurrentVersionHistory(currentResourceHistory);
      if (StringUtils.equals("view", currentResourceHistory.getResourceCategory())) {
         List<LcdpModulePageCompBean> currentModulePageCompList = this.lcdpModulePageCompService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", currentResourceHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
         currentModulePageCompList.stream()
            .filter(e -> !StringUtils.isEmpty(e.getConfigExtend()))
            .forEach(e -> e.setConfig(e.getConfig() + e.getConfigExtend()));
         compareDTO.setCurrentModulePageCompList(currentModulePageCompList);
      }

      if (currentResourceHistory.getVersion() == 1L) {
         return compareDTO;
      } else {
         Long previousVersion = currentResourceHistory.getVersion() - 1L;
         List<LcdpResourceHistoryBean> previousResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance().match("RESOURCEID", id).filter(MatchPattern.SEQ).match("VERSION", previousVersion).filter(MatchPattern.SEQ),
               new Order[0]
            );
         LcdpResourceHistoryBean previousRsourceHistory = previousResourceHistoryList.stream().findFirst().get();
         compareDTO.setPreviousVersionHistory(previousRsourceHistory);
         if (StringUtils.equals("view", previousRsourceHistory.getResourceCategory())) {
            List<LcdpModulePageCompBean> previousModulePageCompList = this.lcdpModulePageCompService
               .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", previousRsourceHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
            previousModulePageCompList.stream()
               .filter(e -> !StringUtils.isEmpty(e.getConfigExtend()))
               .forEach(e -> e.setConfig(e.getConfig() + e.getConfigExtend()));
            compareDTO.setPreviousModulePageCompList(previousModulePageCompList);
         }

         return compareDTO;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void activateResource(String resourceId, Long activateVersion) {
      LcdpResourceBean resource = this.selectById(Long.valueOf(resourceId));
      resource.setEffectVersion(activateVersion);
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resourceId).filter(MatchPattern.SEQ), new Order[0]);
      LcdpResourceHistoryBean resourceHistory = resourceHistoryList.stream().filter(his -> activateVersion.equals(his.getVersion())).findFirst().get();
      resource.setContent(resourceHistory.getContent());
      resourceHistoryList.forEach(history -> {
         if (activateVersion.equals(history.getVersion())) {
            history.setEffectFlag("yes");
         } else {
            history.setEffectFlag("no");
         }
      });
      if (StringUtils.equals("java", resource.getResourceCategory())) {
         resource.setClassContent(
            this.replaceClassName(resource.getPath(), resource.getContent(), resource.getResourceName(), "v" + resource.getEffectVersion(), null)
         );
         resource.setClassName(JavaCompilerManager.getClassName(resource.getClassContent()));
      }

      if (StringUtils.equals("mapper", resource.getResourceCategory())) {
         LcdpMapperUtils.loadMapper(resource.getPath(), true, resource.getContent());
         LcdpMapperUtils.syncMapper(resource.getPath(), true, resource.getContent());
      }

      this.getDao().update(resource, new String[]{"EFFECTVERSION", "CONTENT", "CLASSCONTENT"});
      this.lcdpResourceHistoryService.getDao().update(resourceHistoryList, "EFFECTFLAG");
      List<LcdpResourceDocumentBean> updateDocumentList = new ArrayList<>();
      resourceHistoryList.stream().forEach(e -> updateDocumentList.add(new LcdpResourceDocumentBean(e)));
      LuceneIndexManager.updateIndex(updateDocumentList);
      if ("java".equals(resource.getResourceCategory())) {
         LcdpResourceCache.put(resource.getPath(), resource);
         this.insertServerScriptMethod(resourceHistory);
      }

      if ("view".equals(resource.getResourceCategory())) {
         List<LcdpModulePageCompBean> pageCompList = this.lcdpModulePageCompService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
         LcdpResourceCache.evictPageI18n(resource.getId());
         List<Long> resourceHistoryIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
         List<LcdpModulePageI18nBean> pageI18nBeanList = this.lcdpModulePageI18nService
            .selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistoryIdList).filter(MatchPattern.OR), new Order[0]);
         if (pageI18nBeanList.isEmpty()) {
            this.lcdpModulePageI18nService.batchDealHistoryPageCompI18n(resourceHistoryList, null);
         }
      }
   }

   @Override
   public List<LcdpResourceTreeNodeDTO> selectExportTree(RestJsonWrapperBean wrapper) {
      List<LcdpResourceVersionBean> resourceVersionList = wrapper.parse(LcdpResourceVersionBean.class);
      resourceVersionList = resourceVersionList.stream()
         .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(a -> a.getResourceId()))), ArrayList::new));
      List<LcdpResourceVersionBean> resourceTempList = new ArrayList<>();
      List<Long> resourceIdList = new ArrayList<>();
      List<String> tableNameList = new ArrayList<>();
      List<String> viewNameList = new ArrayList<>();
      resourceVersionList.forEach(version -> {
         if ("table".equals(version.getResourceCategory())) {
            tableNameList.add(version.getResourceId());
         } else if ("db-view".equals(version.getResourceCategory())) {
            viewNameList.add(version.getResourceId());
         } else {
            resourceTempList.add(version);
         }
      });
      List<LcdpResourceVersionBean> categoryList = resourceTempList.stream()
         .filter(r -> StringUtils.equals("category", r.getResourceCategory()))
         .collect(Collectors.toList());
      if (!categoryList.isEmpty()) {
         List<Long> categoryIdList = categoryList.stream().map(LcdpResourceVersionBean::getResourceId).map(Long::valueOf).collect(Collectors.toList());
         List<LcdpResourceBean> moduleList = this.selectListByFilter(
            SearchFilter.instance().match("PARENTID", categoryIdList).filter(MatchPattern.OR).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
         );
         List<Long> moduleIdList = moduleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceBean> scriptAndPageList = this.selectListByFilter(
            SearchFilter.instance()
               .match("PARENTID", moduleIdList)
               .filter(MatchPattern.OR)
               .match("DELETEFLAG", "0")
               .filter(MatchPattern.EQ)
               .match("EFFECTVERSION", null)
               .filter(MatchPattern.DIFFER),
            new Order[0]
         );
         scriptAndPageList = scriptAndPageList.stream().filter(r -> !this.noReturnDataFilter(r)).collect(Collectors.toList());
         scriptAndPageList = this.dealTreeMapper(scriptAndPageList);
         resourceIdList.addAll(scriptAndPageList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList()));
      }

      List<LcdpResourceVersionBean> moduleList = resourceTempList.stream()
         .filter(r -> StringUtils.equals("module", r.getResourceCategory()))
         .collect(Collectors.toList());
      if (!moduleList.isEmpty()) {
         List<Long> moduleIdList = moduleList.stream().map(LcdpResourceVersionBean::getResourceId).map(Long::valueOf).collect(Collectors.toList());
         List<LcdpResourceBean> scriptAndPageList = this.selectListByFilter(
            SearchFilter.instance()
               .match("PARENTID", moduleIdList)
               .filter(MatchPattern.OR)
               .match("DELETEFLAG", "0")
               .filter(MatchPattern.EQ)
               .match("EFFECTVERSION", null)
               .filter(MatchPattern.DIFFER),
            new Order[0]
         );
         scriptAndPageList = scriptAndPageList.stream().filter(r -> !this.noReturnDataFilter(r)).collect(Collectors.toList());
         scriptAndPageList = this.dealTreeMapper(scriptAndPageList);
         resourceIdList.addAll(scriptAndPageList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList()));
      }

      List<String> versionTypeList = Arrays.asList("page", "client", "server", "mapper");
      List<LcdpResourceVersionBean> scriptAndPageList = resourceVersionList.stream()
         .filter(r -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(r.getResourceCategory()) || versionTypeList.contains(r.getResourceCategory()))
         .collect(Collectors.toList());
      resourceIdList.addAll(scriptAndPageList.stream().map(LcdpResourceVersionBean::getResourceId).map(Long::valueOf).collect(Collectors.toList()));
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(SearchFilter.instance().match("TABLENAME", tableNameList).filter(MatchPattern.OR));
      List<Map<String, Object>> tableInfoTest = this.lcdpDatabaseDao.selectTableInfoByCondition(parameter);
      List<LcdpTableBean> tableList = tableInfoTest.stream()
         .map(table -> PersistableHelper.mapToPersistable((Map<String, Object>)table, LcdpTableBean.class))
         .collect(Collectors.toList());
      List<LcdpViewBean> viewList = this.lcdpDatabaseService.selectPhysicalViewInfoList(viewNameList);
      return this.bulidResourceTreeNodeDTOS(resourceIdList, tableList, viewList);
   }

   @Override
   public String export(RestJsonWrapperBean wrapper, LcdpCheckoutRecordBean checkoutRecord) {
      String exportSysClientJsFlag = wrapper.getParamValue("exportSysClientJsFlag");
      LcdpResourceCheckoutConfigDTO checkoutConfigDTO = new LcdpResourceCheckoutConfigDTO();
      checkoutConfigDTO.setExportSysClientJsFlag(exportSysClientJsFlag);
      if (LcdpScriptUtils.validateCurrentDBMybatisMapper()) {
         checkoutConfigDTO.setExportEnvironmentDBType(this.current_environment_database);
      }

      List<LcdpResourceBean> exportResourceList = new ArrayList<>();
      List<LcdpTableDTO> tableDTOList = new ArrayList<>();
      List<LcdpResourceVersionBean> resourceVersionList = wrapper.parse(LcdpResourceVersionBean.class);
      List<String> resourceIdStrList = new ArrayList<>();
      List<String> tableNameList = new ArrayList<>();
      List<String> viewNameList = new ArrayList<>();
      resourceVersionList.forEach(version -> {
         if ("table".equals(version.getResourceCategory())) {
            tableNameList.add(version.getResourceId());
         } else if ("db-view".equals(version.getResourceCategory())) {
            viewNameList.add(version.getResourceId());
         } else {
            resourceIdStrList.add(version.getResourceId());
         }
      });
      List<Long> resourceIdList = resourceIdStrList.stream().map(Long::valueOf).collect(Collectors.toList());
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(SearchFilter.instance().match("TABLENAME", tableNameList).filter(MatchPattern.OCISEQ));
      List<Map<String, Object>> tableInfoList = this.lcdpDatabaseDao.selectTableInfoByCondition(parameter);
      List<LcdpTableBean> tableList = tableInfoList.stream()
         .map(table -> PersistableHelper.mapToPersistable((Map<String, Object>)table, LcdpTableBean.class))
         .collect(Collectors.toList());
      List<LcdpViewBean> viewList = this.lcdpDatabaseService.selectPhysicalViewInfoList(viewNameList);
      List<LcdpResourceTreeNodeDTO> treeNodeDtoList = this.bulidResourceTreeNodeDTOS(resourceIdList, tableList, viewList);
      if (!resourceIdList.isEmpty()) {
         exportResourceList = this.getExportedResources(resourceIdList);
         List<Long> idList = exportResourceList.stream()
            .filter(his -> "view".equals(his.getResourceCategory()))
            .map(his -> Long.valueOf(his.getExt$Item("resourcehistoryid")))
            .collect(Collectors.toList());
         if (!idList.isEmpty()) {
         }
      }

      if (!tableNameList.isEmpty()) {
         tableDTOList = this.lcdpTableService.selectPhysicalTableInfoList(tableNameList);
      }

      if (!resourceIdList.isEmpty()
         || !tableNameList.isEmpty()
         || !viewNameList.isEmpty()
         || !StringUtils.isEmpty(exportSysClientJsFlag) && StringUtils.equals("1", exportSysClientJsFlag)) {
         LcdpResourceExportDTO exportDTO = new LcdpResourceExportDTO();
         exportDTO.setExportLog(checkoutRecord.getCheckoutNote());
         exportDTO.setTreeNodeDtoList(treeNodeDtoList);
         exportDTO.setExportResourceList(exportResourceList);
         exportDTO.setTableDTOList(tableDTOList);
         exportDTO.setViewList(viewList);
         checkoutRecord.setContent(JSONObject.toJSONString(treeNodeDtoList));
         if (StringUtils.equals("1", exportSysClientJsFlag)) {
            String content = checkoutRecord.getContent();
            JSONArray treeNodeArray = JSONObject.parseArray(content);
            LcdpResourceTreeNodeDTO clientJS = new LcdpResourceTreeNodeDTO();
            clientJS.setName(LcdpConstant.SYS_CLIENT_JS);
            clientJS.setDesc(LcdpConstant.SYS_CLIENT_JS);
            clientJS.setId(LcdpConstant.SYS_CLIENT_JS_ID);
            clientJS.setType("js");
            treeNodeArray.add(0, clientJS);
            checkoutRecord.setContent(JSONObject.toJSONString(treeNodeArray));
         }

         return this.createExportFile(exportDTO, checkoutConfigDTO, checkoutRecord);
      } else {
         return null;
      }
   }

   @Override
   public LcdpExportLogFileDTO importAnalyse(RestJsonWrapperBean wrapper) {
      Map<String, String> paramMap = wrapper.getParamMap();
      CoreFileBean coreFileBean = null;
      if (paramMap.get("fileId") != null) {
         Long fileId = Long.valueOf(paramMap.get("fileId"));
         coreFileBean = this.coreFileService.selectById(fileId);
      }

      if (paramMap.get("checkinId") != null) {
         coreFileBean = this.coreFileService.selectFileListByTargetIds("T_LCDP_CHECKIN_RECORD$" + paramMap.get("checkinId")).stream().findFirst().get();
      }

      Path filePath = CoreFileUtils.getLocalPath(coreFileBean);
      List<LcdpResourceVersionBean> resourceVersionList = wrapper.parse(LcdpResourceVersionBean.class);
      List<String> resourceIdStrList = new ArrayList<>();
      List<String> tableNameList = new ArrayList<>();
      List<String> viewNameList = new ArrayList<>();
      resourceVersionList.forEach(version -> {
         if ("table".equals(version.getResourceCategory())) {
            tableNameList.add(version.getResourceId());
         } else if ("db-view".equals(version.getResourceCategory())) {
            viewNameList.add(version.getResourceId());
         } else if (!"table".equals(version.getResourceName()) && !"db-view".equals(version.getResourceName())) {
            resourceIdStrList.add(version.getResourceId());
         }
      });
      Map<String, String> fileMap = new HashMap<>();
      fileMap = this.unZip(filePath, fileMap);
      Map<String, LcdpAnalysisResultDTO> tableResult = new HashMap<>();
      Map<String, LcdpAnalysisResultDTO> viewResult = new HashMap<>();
      if (!tableNameList.isEmpty()) {
         tableResult = this.lcdpTableService.analysisTableInfo(tableNameList, fileMap);
      }

      if (!viewNameList.isEmpty()) {
         viewResult = this.lcdpViewService.analysisViewInfo(viewNameList, fileMap);
      }

      Map<String, Object> importDataMap = this.analysisFileContent(resourceIdStrList, tableNameList, viewNameList, fileMap);
      List<LcdpResourceBean> resourceList = (List<LcdpResourceBean>)importDataMap.get("resource");

      for (int i = 0; i < resourceList.size(); i++) {
         LcdpResourceBean resource = resourceList.get(i);
         if ("module".equals(resource.getResourceCategory())) {
            this.bulidFolder(resourceList, resource);
         }
      }

      LcdpExportLogFileDTO exportLogFileDTO = new LcdpExportLogFileDTO();
      List<LcdpResourceTreeNodeDTO> resourceDTOList = this.bulidAnalysisTreeNodeDTOS(
         tableResult, viewResult, resourceList, (List<LcdpTableDTO>)importDataMap.get("table"), (List<LcdpViewBean>)importDataMap.get("db-view")
      );
      exportLogFileDTO.setTreeNodeDTOList(resourceDTOList);
      String exportLog = fileMap.get("exportLog");
      JSONObject exportLogJson = JSON.parseObject(exportLog);
      String checkoutConfig = fileMap.get("checkoutConfig");
      if (!StringUtils.isEmpty(checkoutConfig)) {
         LcdpResourceCheckoutConfigDTO checkoutConfigDTO = JsonUtils.parse(checkoutConfig, LcdpResourceCheckoutConfigDTO.class);
         exportLogFileDTO.setResourceCheckoutConfigDTO(checkoutConfigDTO);
      }

      String log = exportLogJson.getString("log");
      exportLogFileDTO.setLog(log);
      exportLogFileDTO.setCheckoutRecordNo(exportLogJson.getString("checkoutRecordNo"));
      String tree = JSONObject.toJSONString(resourceDTOList);
      JSONArray analysisTreeArray = JSON.parseArray(tree);
      analysisTreeArray.add(log);
      exportLogFileDTO.setFileId(coreFileBean.getId());
      Map<String, Long> fileIdMap = new HashMap<>();
      fileIdMap.put("fileId", coreFileBean.getId());
      analysisTreeArray.add(fileIdMap);
      boolean allowImportFlag = true;
      Map<String, Boolean> allowImportMap = new HashMap<>();
      allowImportMap.put("allowImport", true);
      if (!tableResult.isEmpty()) {
         for (String tableName : tableResult.keySet()) {
            Boolean allowImport = tableResult.get(tableName).getEnable();
            if (!allowImport) {
               allowImportFlag = false;
               allowImportMap.put("allowImport", allowImport);
               break;
            }
         }
      }

      analysisTreeArray.add(allowImportMap);
      exportLogFileDTO.setAllowImport(allowImportFlag);
      return exportLogFileDTO;
   }

   @Override
   public LcdpResourceCompareDTO importResourcesCompare(RestJsonWrapperBean wrapper) {
      LcdpResourceCompareDTO compareDTO = new LcdpResourceCompareDTO();
      Map<String, String> paramMap = wrapper.getParamMap();
      Long fileId = Long.valueOf(paramMap.get("fileId"));
      CoreFileBean coreFileBean = this.coreFileService.selectById(fileId);
      Path filePath = CoreFileUtils.getLocalPath(coreFileBean);
      List<LcdpResourceVersionBean> resourceVersionList = wrapper.parse(LcdpResourceVersionBean.class);
      LcdpResourceVersionBean resourceVersion = resourceVersionList.stream().findFirst().orElse(null);
      if (null == resourceVersion) {
         return null;
      } else {
         Map<String, String> fileMap = new HashMap<>();
         fileMap = this.unZip(filePath, fileMap);
         if (StringUtils.equals("table", resourceVersion.getResourceCategory())) {
            String tableJson = fileMap.get(resourceVersion.getResourceId());
            LcdpTableDTO tableDTO = JsonUtils.parse(tableJson, LcdpTableDTO.class);
            String sql = this.lcdpTableService.generateCreateSql(tableDTO);
            LcdpTableBean importTable = new LcdpTableBean();
            importTable.setSql(sql);
            List<LcdpTableBean> historyTableList = this.lcdpTableService
               .selectListByFilter(
                  SearchFilter.instance()
                     .match("TABLENAME", tableDTO.getTableName())
                     .filter(MatchPattern.SEQ)
                     .match("SUBMITFLAG", "yes")
                     .filter(MatchPattern.SEQ),
                  new Order[0]
               );
            LcdpTableBean currentTable = historyTableList.stream().max(Comparator.comparing(LcdpTableBean::getVersion)).orElse(null);
            compareDTO.setCurrentTable(importTable);
            compareDTO.setPreviousTable(currentTable);
         } else {
            if (StringUtils.equals(LcdpConstant.SYS_CLIENT_JS_ID, resourceVersion.getResourceId())) {
               String resourceJson = fileMap.get("sysClientJs");
               LcdpGlobalConfigBean sysClientJs = JsonUtils.parse(resourceJson, LcdpGlobalConfigBean.class);
               compareDTO.setCurrentGlobalConfigHistory(sysClientJs);
               LcdpGlobalConfigBean previousSysClientJs = this.lcdpGlobalConfigService.selectConfigContent("SYS_CLIENT_JS");
               compareDTO.setPreviousGlobalConfigHistory(previousSysClientJs);
               return compareDTO;
            }

            String resourceJson = fileMap.get(resourceVersion.getResourceId());
            LcdpResourceBean importResource = JsonUtils.parse(resourceJson, LcdpResourceBean.class);
            LcdpResourceHistoryBean importResourceHistory = new LcdpResourceHistoryBean();
            BeanUtils.copyProperties(importResource, importResourceHistory);
            importResourceHistory.setResourceId(importResource.getId());
            compareDTO.setCurrentVersionHistory(importResourceHistory);
            if (StringUtils.equals("view", importResource.getResourceCategory())) {
               String modulePageCompJson = fileMap.get(resourceVersion.getResourceId() + "view");
               if (!StringUtils.isEmpty(modulePageCompJson)) {
                  List<LcdpModulePageCompBean> modulePageCompBeans = JSON.parseArray(modulePageCompJson).toJavaList(LcdpModulePageCompBean.class);
                  modulePageCompBeans.stream()
                     .filter(e -> !StringUtils.isEmpty(e.getConfigExtend()))
                     .forEach(e -> e.setConfig(e.getConfig() + e.getConfigExtend()));
                  compareDTO.setCurrentModulePageCompList(modulePageCompBeans);
               }
            }

            String path = importResource.getPath();
            List<LcdpResourceBean> resourceList = this.selectListByFilter(
               SearchFilter.instance().match("PATH", path).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
            );
            LcdpResourceBean existResource = resourceList.stream().findFirst().orElse(null);
            if (null == existResource) {
               return compareDTO;
            }

            List<LcdpResourceHistoryBean> historyList = this.lcdpResourceHistoryService
               .selectListByFilter(SearchFilter.instance().match("RESOURCEID", existResource.getId()).filter(MatchPattern.EQ), new Order[0]);
            LcdpResourceHistoryBean resourceHistory = null;
            if (ObjectUtils.isEmpty(existResource.getEffectVersion())) {
               resourceHistory = historyList.stream().findFirst().get();
            } else {
               resourceHistory = historyList.stream().filter(h -> existResource.getEffectVersion().equals(h.getVersion())).findFirst().get();
            }

            compareDTO.setPreviousVersionHistory(resourceHistory);
            if (StringUtils.equals("view", resourceHistory.getResourceCategory())) {
               List<LcdpModulePageCompBean> modulePageCompList = this.lcdpModulePageCompService
                  .selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
               modulePageCompList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getConfigExtend()))
                  .forEach(e -> e.setConfig(e.getConfig() + e.getConfigExtend()));
               compareDTO.setPreviousModulePageCompList(modulePageCompList);
            }
         }

         return compareDTO;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void dealPageComps(RestJsonWrapperBean wrapper) {
      List<LcdpResourceBean> resourceList = this.selectListByFilter(
         SearchFilter.instance()
            .match("RESOURCECATEGORY", "view")
            .filter(MatchPattern.EQ)
            .match("EFFECTVERSION", null)
            .filter(MatchPattern.DIFFER)
            .match("DELETEFLAG", "0")
            .filter(MatchPattern.EQ),
         new Order[0]
      );
      List<Long> resourceIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(
            SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR).match("EFFECTFLAG", "1").filter(MatchPattern.EQ), new Order[0]
         );
      List<Long> resourceHistoryIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
      List<LcdpModulePageCompBean> pageCompList = this.lcdpModulePageCompService
         .selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistoryIdList).filter(MatchPattern.OR), new Order[0]);
      List<String> dealTypeList = Arrays.asList("Grid", "Uploader", "WindowToolbar", "GridToolbar");
      List<LcdpModulePageCompBean> dealPageCompList = pageCompList.stream()
         .filter(compx -> dealTypeList.contains(compx.getType()))
         .collect(Collectors.toList());
      dealPageCompList.forEach(compx -> {
         String config = compx.getConfig() + (StringUtils.isEmpty(compx.getConfigExtend()) ? "" : compx.getConfigExtend());
         JSONObject configObject = JSON.parseObject(config);
         String var4x = compx.getType();
         switch (var4x) {
            case "Grid":
               configObject.remove("filter");
               configObject.remove("toolbarAlign");
               configObject.remove("editorInvisible");
               configObject.remove("columnsFill");
               configObject.remove("contentAlign");
            case "Uploader":
               configObject.remove("toolbarAlign");
               break;
            case "WindowToolbar":
               configObject.remove("toolbarAlign");
               break;
            case "GridToolbar":
               configObject.remove("toolbarAlign");
         }

         this.cutConfig(configObject.toJSONString(), compx);
      });
      this.lcdpModulePageCompService.getDao().update(dealPageCompList, "CONFIG", "CONFIGEXTEND");
      List<LcdpResourceDocumentBean> documentList = new ArrayList<>();

      for (LcdpModulePageCompBean comp : dealPageCompList) {
         LcdpResourceHistoryBean resourceHistory = resourceHistoryList.stream()
            .filter(f -> ObjectUtils.equals(f.getId(), comp.getModulePageHistoryId()))
            .findFirst()
            .orElse(null);
         if (resourceHistory != null) {
            String content = comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend());
            documentList.add(
               new LcdpResourceDocumentBean(
                  resourceHistory.getId() + "$$" + comp.getId(),
                  resourceHistory.getResourceName(),
                  resourceHistory.getPath(),
                  "comp",
                  content,
                  comp.getModulePageVersion(),
                  "0",
                  resourceHistory.getCreatedById()
               )
            );
         }
      }

      LuceneIndexManager.updateIndex(documentList);
   }

   @Override
   public LcdpExportLogFileDTO importByFile(Long fileId, RestJsonWrapperBean wrapper) {
      LcdpExportLogFileDTO exportLogFileDTO = new LcdpExportLogFileDTO();
      CoreFileBean coreFileBean = this.coreFileService.selectById(fileId);
      Path filePath = CoreFileUtils.getLocalPath(coreFileBean);
      Map<String, Long> fileIdMap = new HashMap<>();
      fileIdMap.put("fileId", fileId);
      Map<String, String> dataMap = new HashMap<>();
      Map<String, String> fileMap = this.unZip(filePath, dataMap);
      String exportLog = fileMap.get("exportLog");
      String checkoutConfig = fileMap.get("checkoutConfig");
      if (!StringUtils.isEmpty(checkoutConfig)) {
         LcdpResourceCheckoutConfigDTO checkoutConfigDTO = JsonUtils.parse(checkoutConfig, LcdpResourceCheckoutConfigDTO.class);
         exportLogFileDTO.setResourceCheckoutConfigDTO(checkoutConfigDTO);
      }

      JSONObject exportLogJson = JSON.parseObject(exportLog);
      List<LcdpResourceTreeNodeDTO> treeNodeDTOList = (List<LcdpResourceTreeNodeDTO>)exportLogJson.get("treeNodeDTOList");
      String log = exportLogJson.getString("log");
      exportLogFileDTO.setFileId(fileId);
      exportLogFileDTO.setLog(log);
      exportLogFileDTO.setTreeNodeDTOList(treeNodeDTOList);
      exportLogFileDTO.setCheckoutRecordNo(exportLogJson.getString("checkoutRecordNo"));
      return exportLogFileDTO;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public String importData(RestJsonWrapperBean wrapper) {
      String checkinIdStr = wrapper.getParamValue("checkinId");
      String exportSysClientJsFlag = wrapper.getParamValue("exportSysClientJsFlag");
      CoreFileBean coreFileBean = this.coreFileService.selectFileListByTargetIds("T_LCDP_CHECKIN_RECORD$" + checkinIdStr).stream().findFirst().get();
      Path filePath = CoreFileUtils.getLocalPath(coreFileBean);
      Map<String, String> paramMap = wrapper.getParamMap();
      String importLog = paramMap.get("importLog");
      StringBuilder importJavaRecord = new StringBuilder();
      LcdpSubmitLogBean autoSubmitLog = new LcdpSubmitLogBean();
      autoSubmitLog.setId(ApplicationContextHelper.getNextIdentity());
      autoSubmitLog.setCommit(importLog + ":" + I18nHelper.getMessage("T_LCDP_RESOURCE.FILE.IMPORT.AUTO.SUBMIT"));
      LcdpSubmitLogBean submitLog = new LcdpSubmitLogBean();
      submitLog.setId(ApplicationContextHelper.getNextIdentity());
      submitLog.setCommit(importLog);
      this.submitLogService.getDao().insert(submitLog);
      List<LcdpResourceVersionBean> resourceVersionList = wrapper.parse(LcdpResourceVersionBean.class);
      List<String> resourceIdStrList = new ArrayList<>();
      List<String> tableNameList = new ArrayList<>();
      List<String> viewNameList = new ArrayList<>();
      resourceVersionList.forEach(version -> {
         if ("table".equals(version.getResourceCategory())) {
            tableNameList.add(version.getResourceId());
         } else if ("db-view".equals(version.getResourceCategory())) {
            viewNameList.add(version.getResourceId());
         } else {
            resourceIdStrList.add(version.getResourceId());
         }
      });
      Map<String, String> fileMap = new HashMap<>();
      fileMap = this.unZip(filePath, fileMap);
      Map<String, Object> importDataMap = this.analysisFileContent(resourceIdStrList, tableNameList, viewNameList, fileMap);
      List<LcdpResourceBean> resourceList = (List<LcdpResourceBean>)importDataMap.get("resource");
      List<LcdpTableDTO> tableDTOList = (List<LcdpTableDTO>)importDataMap.get("table");
      List<LcdpViewBean> importViewList = (List<LcdpViewBean>)importDataMap.get("db-view");
      List<LcdpModulePageCompBean> pageCompList = (List<LcdpModulePageCompBean>)importDataMap.get("pageComps");
      List<LcdpModulePageI18nBean> pageI18nList = (List<LcdpModulePageI18nBean>)importDataMap.get("pageI18n");
      List<LcdpPageI18nCodeBean> pageDependentI18nList = (List<LcdpPageI18nCodeBean>)importDataMap.get("pageDependentI18n");
      List<CoreCodeCategoryBean> coreCodeCategoryList = (List<CoreCodeCategoryBean>)importDataMap.get("codeCategory");
      List<CoreCodeBean> codeList = (List<CoreCodeBean>)importDataMap.get("code");
      List<CoreAdminSelectConfigBean> coreAdminSelectConfigList = (List<CoreAdminSelectConfigBean>)importDataMap.get("adminSelectConfig");
      List<CoreI18nMessageBean> coreI18nMessageList = (List<CoreI18nMessageBean>)importDataMap.get("i18n");
      Map<Long, List<LcdpModulePageCompBean>> resourceId2PageCompMap = pageCompList.stream()
         .collect(Collectors.groupingBy(LcdpModulePageCompBean::getModulePageId));
      Map<Long, List<LcdpModulePageI18nBean>> resourceId2PageI18nMap = pageI18nList.stream()
         .collect(Collectors.groupingBy(LcdpModulePageI18nBean::getModulePageId));
      Map<Long, List<LcdpPageI18nCodeBean>> resourceId2PageDependentI18nMap = pageDependentI18nList.stream()
         .collect(Collectors.groupingBy(LcdpPageI18nCodeBean::getModulePageId));
      List<LcdpResourceBean> importResourceDataList = new ArrayList<>();
      if (!resourceList.isEmpty()) {
         importResourceDataList = this.importResourceData(
            resourceList, resourceId2PageCompMap, resourceId2PageI18nMap, resourceId2PageDependentI18nMap, submitLog, autoSubmitLog, importJavaRecord
         );
      }

      List<LcdpTableBean> tableList = new ArrayList<>();
      List<LcdpViewBean> viewList = new ArrayList<>();
      if (!tableDTOList.isEmpty()) {
         tableList = this.importTableData(tableNameList, fileMap, submitLog, autoSubmitLog);
      }

      if (!importViewList.isEmpty()) {
         viewList = this.importViewData(viewNameList, fileMap, submitLog, autoSubmitLog);
      }

      List<Long> idList = importResourceDataList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      List<LcdpResourceTreeNodeDTO> treeNodeDTOList = this.bulidResourceTreeNodeDTOS(idList, tableList, viewList);
      this.importSysCode(coreCodeCategoryList, codeList, coreAdminSelectConfigList);
      if (!ObjectUtils.isEmpty(coreI18nMessageList)) {
         this.importSysI18n(coreI18nMessageList);
      }

      if (StringUtils.equals(exportSysClientJsFlag, "1")) {
         this.importSysClientJs(fileMap);
      }

      LcdpResourceImportRecordBean record = new LcdpResourceImportRecordBean();
      record.setId(ApplicationContextHelper.getNextIdentity());
      record.setImportLog(importLog);
      record.setImportContent(JSON.toJSONString(treeNodeDTOList));
      String exportLog = fileMap.get("exportLog");
      JSONObject exportLogJson = JSON.parseObject(exportLog);
      String log = exportLogJson.getString("log");
      record.setExportLog(log);
      EntityHelper.assignCreatedElement(record);
      this.resourceImportRecordService.getDao().insert(record);
      return StringUtils.isEmpty(importJavaRecord.toString())
         ? I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.RESOURCE_IMPORT_SUCCESS")
         : I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.RESOURCE_IMPORT_SUCCESS")
            + I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.JAVA.COMPILER.ERROR")
            + "：/r/n"
            + importJavaRecord;
   }

   @Override
   public LcdpResourceCompareDTO selectHistoryDetail(Long historyId, RestJsonWrapperBean wrapper) {
      LcdpResourceCompareDTO compareDTO = new LcdpResourceCompareDTO();
      String category = wrapper.getParamValue("category");
      if (StringUtils.equals(category, "table")) {
         List<LcdpTableBean> tableList = this.lcdpTableService
            .selectListByFilter(SearchFilter.instance().match("ID", historyId).filter(MatchPattern.EQ), new Order[0]);
         compareDTO.setCurrentTable(tableList.stream().findFirst().orElse(null));
         return compareDTO;
      } else if (StringUtils.equals(category, "db-view")) {
         List<LcdpViewBean> viewList = this.lcdpViewService
            .selectListByFilter(SearchFilter.instance().match("ID", historyId).filter(MatchPattern.EQ), new Order[0]);
         compareDTO.setCurrentView(viewList.stream().findFirst().orElse(null));
         return compareDTO;
      } else {
         LcdpResourceHistoryBean currentHistory = this.lcdpResourceHistoryService.selectById(historyId);
         compareDTO.setCurrentVersionHistory(currentHistory);
         if (StringUtils.equals("view", currentHistory.getResourceCategory())) {
            List<LcdpModulePageCompBean> currentModulePageCompList = this.lcdpModulePageCompService
               .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", currentHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
            currentModulePageCompList.stream()
               .filter(e -> !StringUtils.isEmpty(e.getConfigExtend()))
               .forEach(e -> e.setConfig(e.getConfig() + e.getConfigExtend()));
            compareDTO.setCurrentModulePageCompList(currentModulePageCompList);
         }

         return compareDTO;
      }
   }

   @Override
   public LcdpResourceDTO selectContentByPath(RestJsonWrapperBean wrapper) {
      Map<String, String> paramMap = wrapper.getParamMap();
      String resourcePath = paramMap.get("resourcePath");
      String lcdpEnv = ServletUtils.getCurrentRequest().getHeader("lcdp-env");
      String sunwayMenu = ServletUtils.getCurrentRequest().getHeader("sunway-menu");
      String clientScript = null;
      if (!StringUtils.isEmpty(sunwayMenu)) {
         CoreMenuBean coreMenuBean = this.coreMenuService
            .selectListByFilter(SearchFilter.instance().match("ID", sunwayMenu).filter(MatchPattern.EQ), new Order[0])
            .stream()
            .findFirst()
            .get();
         if (null != coreMenuBean.getBpId()) {
            Long bpId = coreMenuBean.getBpId();
            LcdpBizProcessNodeMenuBean bizProcessNodeMenu = this.bizProcessNodeMenuService
               .selectListByFilter(
                  SearchFilter.instance().match("BPID", bpId).filter(MatchPattern.EQ).match("MENUID", sunwayMenu).filter(MatchPattern.EQ), new Order[0]
               )
               .stream()
               .findFirst()
               .orElse(null);
            if (bizProcessNodeMenu != null) {
               clientScript = bizProcessNodeMenu.getClientScript();
            }
         }
      }

      List<String> queryColumnList = ArrayUtils.asList("ID", "EFFECTVERSION");
      LcdpResourceBean condition = new LcdpResourceBean();
      condition.setPath(resourcePath);
      condition.setDeleteFlag("0");
      LcdpResourceBean resource = this.getDao()
         .selectFirstIfPresent(condition, ArrayUtils.asList("PATH", "DELETEFLAG"), queryColumnList, new Order[]{Order.asc("RESOURCENAME")});
      if ("development".equals(lcdpEnv)) {
         if (resource == null) {
            SearchFilter filter = SearchFilter.instance()
               .match("PATH", resourcePath)
               .filter(MatchPattern.EQ)
               .match("SUBMITFLAG", "no")
               .filter(MatchPattern.SEQ)
               .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.SEQ)
               .match("DELETEFLAG", "0")
               .filter(MatchPattern.EQ);
            LcdpResourceHistoryBean history = this.lcdpResourceHistoryService.selectFirstByFilter(filter, new Order[0]);
            if (history == null) {
               return new LcdpResourceDTO();
            } else {
               LcdpResourceDTO resourceDTO = this.buildLcdpResourceDTO(history);
               resourceDTO.setClientScript(clientScript);
               return resourceDTO;
            }
         } else {
            return this.getDesignResourceDTO(resource);
         }
      } else if (resource == null) {
         return new LcdpResourceDTO();
      } else {
         LcdpResourceHistoryBean historyCondition = new LcdpResourceHistoryBean();
         historyCondition.setResourceId(resource.getId());
         historyCondition.setEffectFlag("yes");
         LcdpResourceHistoryBean resourceHistory = this.lcdpResourceHistoryService
            .getDao()
            .selectFirst(historyCondition, Arrays.asList("ID", "RESOURCEID", "RESOURCENAME", "RESOURCECATEGORY", "CONTENT", "PATH"));
         LcdpResourceDTO resourceDTO = this.buildLcdpResourceDTO(resourceHistory);
         resourceDTO.setClientScript(clientScript);
         return resourceDTO;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @CacheEvict(
      value = {"T_CORE_MENU"},
      key = "'ALL'"
   )
   @Override
   public void publishMenu(RestJsonWrapperBean wrapper) {
      CoreMenuBean coreMenu = wrapper.parseUnique(CoreMenuBean.class);
      if (!StringUtils.isEmpty(coreMenu.getId())) {
         coreMenu.setPublishTime(LocalDateTime.now());
         coreMenu.setPublishUserId(LocalContextHelper.getLoginUserId());
         coreMenu.setPublishUserName(LocalContextHelper.getLoginUserName());
         this.coreMenuService.getDao().update(coreMenu, "PUBLISHTIME", "PUBLISHUSERID", "PUBLISHUSERNAME", "RESOURCEID", "RESOURCENAME", "RESOURCEPATH");
      }
   }

   @Override
   public List<LcdpServerScriptMethodBean> selectServerScriptMethodSelectableList(RestJsonWrapperBean wrapper) {
      Map<String, String> paramMap = wrapper.getParamMap();
      String methodPath = paramMap.get("methodPath");
      String mappingType = paramMap.get("mappingType");
      String methodDesc = paramMap.get("methodDesc");
      String[] queryParam = methodPath.split("\\*");
      SearchFilter filter = SearchFilter.instance();

      for (String param : queryParam) {
         filter.match("METHODPATH", param.toUpperCase()).filter(MatchPattern.CISC).match("DELETEFLAG", "0").filter(MatchPattern.EQ);
      }

      if (!StringUtils.isEmpty(mappingType)) {
         filter.match("MAPPINGTYPE", Arrays.asList(mappingType, MappingType.DEFAULT.name())).filter(MatchPattern.OR);
      }

      if (!StringUtils.isEmpty(methodDesc)) {
         filter.match("METHODDESC", methodDesc).filter(MatchPattern.SC);
      }

      List<LcdpServerScriptMethodBean> backEndScriptMethodList = this.serverScriptMethodService
         .selectListByFilter(filter, new Order[]{Order.asc("METHODPATH")});
      List<LcdpServerScriptMethodBean> removeList = backEndScriptMethodList.stream()
         .filter(bs -> StringUtils.equals(bs.getMethodFlag(), "draft") && !StringUtils.equals(LocalContextHelper.getLoginUserId(), bs.getMethodCreatedById()))
         .collect(Collectors.toList());
      backEndScriptMethodList.removeAll(removeList);
      if (backEndScriptMethodList.size() > 150) {
         backEndScriptMethodList = backEndScriptMethodList.subList(0, 150);
      }

      return backEndScriptMethodList;
   }

   @Override
   public Page<LcdpServerScriptMethodBean> selectServerScriptApiMethodList(RestJsonWrapperBean wrapper) {
      SearchFilter filter = SearchFilter.instance().match("DELETEFLAG", "0").filter(MatchPattern.SEQ).match("METHODFLAG", "draft").filter(MatchPattern.DIFFER);
      return this.serverScriptMethodService.selectPaginationByFilter(filter, wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void revert(RestJsonWrapperBean wrapper) {
      List<LcdpResourceVersionBean> resourceVersionList = wrapper.parse(LcdpResourceVersionBean.class);
      List<Long> resourceIdList = new ArrayList<>();
      List<String> tableNameList = new ArrayList<>();
      List<String> viewNameList = new ArrayList<>();
      resourceVersionList.forEach(resource -> {
         if ("table".equals(resource.getResourceCategory())) {
            tableNameList.add(resource.getResourceId());
         } else if ("db-view".equals(resource.getResourceCategory())) {
            viewNameList.add(resource.getResourceId());
         } else {
            resourceIdList.add(Long.valueOf(resource.getResourceId()));
         }
      });
      this.revert(resourceIdList, tableNameList, viewNameList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void moduleRevert(RestJsonWrapperBean wrapper) {
      LcdpResourceBean module = wrapper.parseUnique(LcdpResourceBean.class);
      Long moduleId = module.getId();
      List<LcdpResourceBean> childList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", moduleId).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
      );
      List<LcdpResourceBean> revertList = childList.stream().filter(child -> !this.noReturnDataFilter(child)).collect(Collectors.toList());
      List<Long> resourceIdlist = revertList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      this.revert(resourceIdlist, null, null);
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW
   )
   @Override
   public void saveContent(LcdpResourceHistoryBean resourceHistory) {
      this.lcdpResourceHistoryService.getDao().update(resourceHistory, "CONTENT", "MODIFYVERSION", "CLASSCONTENT");
   }

   private List<LcdpResourceTreeNodeDTO> selectTreeBySkip(String resourceIdStr, Map<Long, LcdpResourceCheckoutRecordBean> resourceId2CheckOutMap) {
      List<LcdpResourceBean> resourceList = this.selectListByFilter(
         SearchFilter.instance()
            .match("PARENTID", null)
            .filter(MatchPattern.EQ)
            .match("RESOURCECATEGORY", "category")
            .filter(MatchPattern.EQ)
            .match("DELETEFLAG", "0")
            .filter(MatchPattern.EQ),
         new Order[]{Order.asc("RESOURCENAME")}
      );
      resourceList.forEach(resource -> resource.setExt$Item("checkoutstatus", resourceId2CheckOutMap.get(resource.getId()) == null ? null : "1"));
      Long resourceId = Long.valueOf(resourceIdStr);
      LcdpResourceBean lcdpResource = this.selectByIdIfPresent(resourceId);
      if (lcdpResource == null) {
         LcdpResourceHistoryBean resourceHistory = this.lcdpResourceHistoryService.selectByIdIfPresent(resourceId);
         if (resourceHistory == null) {
            return null;
         }

         lcdpResource = this.selectByIdIfPresent(resourceHistory.getResourceId());
      }

      if (StringUtils.equals("1", lcdpResource.getDeleteFlag())) {
         return null;
      } else {
         Long moduleId = StringUtils.equals("module", lcdpResource.getResourceCategory()) ? lcdpResource.getId() : lcdpResource.getParentId();
         List<LcdpResourceBean> pageAndScriptList = this.selectListByFilter(
            SearchFilter.instance().match("PARENTID", moduleId).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ),
            new Order[]{Order.asc("RESOURCENAME")}
         );
         List<LcdpResourceBean> childList = pageAndScriptList.stream().filter(ps -> !this.noReturnDataFilter(ps)).collect(Collectors.toList());
         childList = this.dealTreeMapper(childList);
         List<String> childIdList = childList.stream().map(r -> r.getId().toString()).collect(Collectors.toList());
         List<LcdpResourceLockBean> lockedResourceList = this.lcdpResourceLockService
            .selectListByFilter(
               SearchFilter.instance().match("LOCKUSERID", null).filter(MatchPattern.DIFFER).match("RESOURCEID", childIdList).filter(MatchPattern.OR),
               new Order[0]
            );
         Map<String, String> resourceLockStatusMap = lockedResourceList.stream()
            .collect(Collectors.toMap(LcdpResourceLockBean::getResourceId, LcdpResourceLockBean::getLockUserId, (v1, v2) -> v1));
         LcdpResourceBean module = this.selectById(moduleId);
         Long categoryId = module.getParentId();
         List<LcdpResourceBean> moduleList = this.selectListByFilter(
            SearchFilter.instance().match("PARENTID", categoryId).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ),
            new Order[]{Order.asc("RESOURCENAME")}
         );
         List<Long> parentIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         parentIdList.addAll(moduleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList()));
         List<LcdpTreeAuxiliaryDTO> treeAuxiliaryDTOList = this.getDao()
            .selectTreeChildQtyByParentId(parentIdList)
            .stream()
            .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, LcdpTreeAuxiliaryDTO.class))
            .collect(Collectors.toList());
         List<Long> resultList = treeAuxiliaryDTOList.stream().map(data -> data.getParentId()).collect(Collectors.toList());
         moduleList.forEach(resource -> resource.setExt$Item("checkoutstatus", resourceId2CheckOutMap.get(resource.getId()) == null ? null : "1"));
         resourceList.addAll(moduleList);
         this.bulidFolder(childList, module);
         resourceList.addAll(childList);
         this.dealResourceName(resourceList);
         TreeDescriptor<LcdpResourceBean> descriptor = new TreeDescriptor<>("id", "parentId", "resourceName", "orderNo");
         Map<Long, List<LcdpResourceBean>> parentId2ResourceList = resourceList.stream()
            .filter(resource -> null != resource.getParentId())
            .collect(Collectors.groupingBy(LcdpResourceBean::getParentId));
         descriptor.setParseTreeNodeFunction(
            t -> {
               LcdpResourceTreeNodeDTO resourceDTO = LcdpResourceTreeNodeDTO.of(t);
               if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(t.getResourceCategory())) {
                  resourceDTO.setCheckoutTime(resourceId2CheckOutMap.get(t.getId()) == null ? null : resourceId2CheckOutMap.get(t.getId()).getCheckoutTime());
               }

               resourceDTO.setCheckOutFlag(
                  t.getExt$Item("checkoutstatus") != null
                     ? t.getExt$Item("checkoutstatus")
                     : (
                        resourceLockStatusMap.containsKey(resourceDTO.getId())
                              && StringUtils.equals(resourceLockStatusMap.get(resourceDTO.getId()), LocalContextHelper.getLoginUserId())
                           ? "1"
                           : "0"
                     )
               );
               resourceDTO.setOtherUserCheckOutFlag(
                  resourceLockStatusMap.containsKey(resourceDTO.getId())
                        && !StringUtils.equals(resourceLockStatusMap.get(resourceDTO.getId()), LocalContextHelper.getLoginUserId())
                     ? "1"
                     : "0"
               );
               resourceDTO.setLeafFlag(true);
               if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(t.getResourceCategory())) {
                  resourceDTO.setResourceStatus(null == t.getEffectVersion() ? "new" : "valid");
               }

               if ("mapper".equals(t.getResourceCategory())) {
                  resourceDTO.setResourceStatus(null == t.getEffectVersion() ? "new" : "valid");
               }

               if (resultList.contains(t.getId()) || "module".equals(t.getResourceCategory())) {
                  resourceDTO.setLeafFlag(false);
               }

               if (!ObjectUtils.isEmpty(parentId2ResourceList.get(t.getId()))) {
                  resourceDTO.setLeafFlag(false);
               }

               return resourceDTO;
            }
         );
         List<LcdpResourceTreeNodeDTO> resourceDTOList = TreeHelper.parseTreeNode(resourceList, descriptor, LcdpResourceTreeNodeDTO.class);
         TreeHelper.updateChildQty(resourceDTOList);
         return resourceDTOList;
      }
   }

   private String dealResourceName(LcdpResourceBean resource) {
      String resourceName = resource.getResourceName();
      if (!resourceName.toLowerCase().startsWith("lcdp")) {
         resourceName = "Lcdp" + resourceName;
      } else {
         resourceName = "Lcdp" + resourceName.substring(4);
      }

      return resourceName;
   }

   private void revert(List<Long> resourceIdList, List<String> tableNameList, List<String> viewNameList) {
      if (!resourceIdList.isEmpty() || !ObjectUtils.isEmpty(tableNameList) || !ObjectUtils.isEmpty(viewNameList)) {
         List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCEID", resourceIdList)
                  .filter(MatchPattern.OR)
                  .match("SUBMITFLAG", "no")
                  .filter(MatchPattern.SEQ)
                  .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
                  .filter(MatchPattern.SEQ)
                  .match("VERSION", 1)
                  .filter(MatchPattern.DIFFER),
               new Order[0]
            );
         this.lcdpResourceHistoryService.getDao().deleteBy(resourceHistoryList);
         List<String> unLockResourceIdList = resourceHistoryList.stream()
            .map(resourceHistory -> String.valueOf(resourceHistory.getResourceId()))
            .collect(Collectors.toList());
         if (null != tableNameList) {
            unLockResourceIdList.addAll(this.lcdpTableService.revert(tableNameList));
         }

         if (null != viewNameList) {
            unLockResourceIdList.addAll(this.lcdpViewService.revert(viewNameList));
         }

         this.lcdpResourceLockService.unLock(unLockResourceIdList);
         List<LcdpResourceBean> resourceList = this.getDao().selectListByIds(resourceIdList, new Order[0]);
         if (!resourceList.isEmpty()) {
            this.resourceCheckoutRecordService.unCheckoutResource(resourceList);
         }

         List<String> resourceHistoryIdList = resourceHistoryList.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
         LuceneIndexManager.deleteIndex(resourceHistoryIdList);
         this.deleteCompDocumentById(CollectionUtils.emptyList(), resourceHistoryIdList);
      }
   }

   @Override
   public List<LcdpModulePageCompBean> generatePageComps(List<LcdpModulePageCompBean> modulePageCompList) {
      if (ObjectUtils.isEmpty(modulePageCompList)) {
         return new ArrayList<>();
      } else {
         List<LcdpModulePageCompBean> pageCompDataList = modulePageCompList.stream().map(page -> {
            LcdpModulePageCompBean pageCompx = new LcdpModulePageCompBean();
            BeanUtils.copyProperties(page, pageCompx);
            return pageCompx;
         }).collect(Collectors.toList());
         List<LcdpModulePageCompBean> rootCompList = modulePageCompList.stream()
            .filter(comp -> ObjectUtils.isEmpty(comp.getParentId()))
            .collect(Collectors.toList());
         Map<String, LcdpModulePageCompBean> parentCompMap = rootCompList.stream()
            .collect(Collectors.toMap(LcdpModulePageCompBean::getId, Function.identity()));
         List<String> rootIdList = rootCompList.stream().map(LcdpModulePageCompBean::getId).collect(Collectors.toList());
         Map<String, LcdpModulePageCompBean> rootId2RootCompMap = rootCompList.stream()
            .collect(Collectors.toMap(LcdpModulePageCompBean::getId, Function.identity()));
         Map<String, List<LcdpModulePageCompBean>> parentId2PageCompListMap = modulePageCompList.stream()
            .filter(comp -> null != comp.getParentId())
            .collect(Collectors.groupingBy(LcdpModulePageCompBean::getParentId));
         List<LcdpModulePageCompBean> childrenCompList = new ArrayList<>();
         List<String> singleRootIdList = new ArrayList<>();

         for (String rootId : rootIdList) {
            List<LcdpModulePageCompBean> pageCompBeanList = parentId2PageCompListMap.get(rootId);
            if (!ObjectUtils.isEmpty(pageCompBeanList)) {
               childrenCompList.addAll(pageCompBeanList);
            } else {
               singleRootIdList.add(rootId);
            }
         }

         List<LcdpModulePageCompBean> targetPageCompList = new ArrayList<>();
         if (!singleRootIdList.isEmpty()) {
            for (String rootIdx : singleRootIdList) {
               LcdpModulePageCompBean singleComp = rootId2RootCompMap.get(rootIdx);
               singleComp.setId(StringUtils.randomUUID());
               targetPageCompList.add(singleComp);
            }
         }

         Map<String, List<LcdpModulePageCompBean>> compListByParentIdMap = childrenCompList.stream()
            .collect(Collectors.groupingBy(LcdpModulePageCompBean::getParentId));
         Map<String, String> idMap = new HashMap<>();
         compListByParentIdMap.forEach(
            (parentId, childrenList) -> {
               LcdpModulePageCompBean parentComp = parentCompMap.get(parentId);
               parentComp.setId(StringUtils.randomUUID());
               JSONObject configx = JSON.parseObject(
                  parentComp.getConfig() + (StringUtils.isEmpty(parentComp.getConfigExtend()) ? "" : parentComp.getConfigExtend())
               );
               childrenList.forEach(child -> {
                  String id = StringUtils.randomUUID();
                  idMap.put(child.getId(), id);
                  child.setId(id);
                  child.setParentId(parentComp.getId());
               });
               Set<String> keySet = configx.keySet();

               for (String key : keySet.stream()
                  .filter(keyx -> keyx.equals("childrenWidgetId") || keyx.endsWith("_childrenWidgetId"))
                  .collect(Collectors.toList())) {
                  JSONArray childrenWidgetId = (JSONArray)configx.get(key);
                  if (!ObjectUtils.isEmpty(childrenWidgetId)) {
                     for (int i = 0; i < childrenWidgetId.size(); i++) {
                        childrenWidgetId.set(i, idMap.get(childrenWidgetId.getString(i)));
                     }

                     configx.put(key, childrenWidgetId);
                  }
               }

               String configJsonx = JSONObject.toJSONString(configx);
               this.cutConfig(configJsonx, parentComp);
               targetPageCompList.add(parentComp);
               targetPageCompList.addAll(childrenList);
            }
         );
         this.updateChildrenComp(idMap, targetPageCompList, parentId2PageCompListMap, pageCompDataList);

         for (LcdpModulePageCompBean pageComp : targetPageCompList) {
            String configStr = pageComp.getConfig();
            if (!StringUtils.isEmpty(configStr)) {
               JSONObject config = JSON.parseObject(pageComp.getConfig() + (StringUtils.isEmpty(pageComp.getConfigExtend()) ? "" : pageComp.getConfigExtend()));
               config.put("uuid", pageComp.getId());
               String configJson = JSONObject.toJSONString(config);
               this.cutConfig(configJson, pageComp);
            }
         }

         List<LcdpModulePageCompBean> shuttleFrameList = targetPageCompList.stream()
            .filter(comp -> "ShuttleFrame".equals(comp.getType()))
            .collect(Collectors.toList());
         if (!shuttleFrameList.isEmpty()) {
            shuttleFrameList.forEach(
               shuttleFrame -> {
                  JSONObject configx = JSON.parseObject(
                     shuttleFrame.getConfig() + (StringUtils.isEmpty(shuttleFrame.getConfigExtend()) ? "" : shuttleFrame.getConfigExtend())
                  );
                  JSONArray childrenWidgetId = configx.getJSONArray("childrenWidgetId");
                  String leftGridId = childrenWidgetId.getString(0);
                  String rightGridId = childrenWidgetId.getString(1);
                  LcdpModulePageCompBean leftGrid = targetPageCompList.stream()
                     .filter(comp -> StringUtils.equals(leftGridId, comp.getId()))
                     .findFirst()
                     .orElse(null);
                  LcdpModulePageCompBean rightGrid = targetPageCompList.stream()
                     .filter(comp -> StringUtils.equals(rightGridId, comp.getId()))
                     .findFirst()
                     .orElse(null);
                  if (leftGrid != null) {
                     JSONObject leftGridConfig = JSON.parseObject(
                        leftGrid.getConfig() + (StringUtils.isEmpty(leftGrid.getConfigExtend()) ? "" : leftGrid.getConfigExtend())
                     );
                     configx.put("columns_childrenWidgetId", leftGridConfig.get("childrenWidgetId"));
                     configx.put("leftToolbar_childrenWidgetId", leftGridConfig.get("toolbar_childrenWidgetId"));
                  }

                  if (rightGrid != null) {
                     JSONObject rightGridConfig = JSON.parseObject(
                        rightGrid.getConfig() + (StringUtils.isEmpty(rightGrid.getConfigExtend()) ? "" : rightGrid.getConfigExtend())
                     );
                     configx.put("rightColumns_childrenWidgetId", rightGridConfig.get("childrenWidgetId"));
                     configx.put("rightToolbar_childrenWidgetId", rightGridConfig.get("toolbar_childrenWidgetId"));
                  }

                  String configJsonx = JSONObject.toJSONString(configx);
                  this.cutConfig(configJsonx, shuttleFrame);
               }
            );
         }

         return targetPageCompList;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertServerScriptMethod(List<LcdpResourceHistoryBean> javaResourceHistoryList) {
      List<LcdpServerScriptMethodBean> serverScriptMethodList = new ArrayList<>();
      List<Long> resourceIdList = new ArrayList<>();
      Map<Long, Method[]> methodMap = javaResourceHistoryList.stream()
         .collect(Collectors.toMap(LcdpResourceHistoryBean::getId, his -> DynamicClassLoader.getInstance().loadSourceCode(his.getClassContent()).getMethods()));

      for (LcdpResourceHistoryBean java : javaResourceHistoryList) {
         for (Method method : (List<Method>)Arrays.stream(methodMap.get(java.getId()))
            .filter(methodx -> methodx.getAnnotation(Mapping.class) != null)
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Method::getName))), ArrayList::new))) {
            LcdpServerScriptMethodBean serverScriptMethod = new LcdpServerScriptMethodBean();
            Mapping annotation = method.getAnnotation(Mapping.class);
            String methodName = method.getName();
            String methodPath = java.getPath() + "." + methodName;
            serverScriptMethod.setId(ApplicationContextHelper.getNextIdentity());
            serverScriptMethod.setServerScriptId(java.getResourceId());
            serverScriptMethod.setMethodName(methodName);
            serverScriptMethod.setMethodPath(methodPath);
            serverScriptMethod.setMethodDesc(annotation.value());
            serverScriptMethod.setMappingType(annotation.type().name());
            serverScriptMethod.setMethodFlag("normal");
            serverScriptMethod.setMethodCreatedById(LocalContextHelper.getLoginUserId());
            if (StringUtils.equals(java.getEffectFlag(), "no") && java.getVersion() == 1L) {
               serverScriptMethod.setMethodFlag("draft");
            }

            resourceIdList.add(java.getResourceId());
            serverScriptMethodList.add(serverScriptMethod);
         }
      }

      int batchSize = this.dialect.getMaxParamQty();
      List<LcdpServerScriptMethodBean> deleteServerScriptMethodList = new ArrayList<>();
      int numBatches = (int)Math.ceil((double)resourceIdList.size() / batchSize);

      for (int i = 0; i < numBatches; i++) {
         int start = i * batchSize;
         int end = Math.min(start + batchSize, resourceIdList.size());
         List<Long> batchIds = resourceIdList.subList(start, end);
         List<LcdpServerScriptMethodBean> methodList = this.serverScriptMethodService
            .selectListByFilter(SearchFilter.instance().match("SERVERSCRIPTID", batchIds).filter(MatchPattern.OR), new Order[0]);
         deleteServerScriptMethodList.addAll(methodList);
      }

      if (!deleteServerScriptMethodList.isEmpty()) {
         List<Long> deleteIdList = deleteServerScriptMethodList.stream().map(LcdpServerScriptMethodBean::getId).collect(Collectors.toList());
         this.serverScriptMethodService.getDao().deleteByIdList(deleteIdList);
      }

      this.serverScriptMethodService.getDao().insert(serverScriptMethodList);
   }

   @Override
   public RestValidationResultBean validateUnique(RestJsonWrapperBean wrapper) {
      String validateUniqueFieldJson = wrapper.getParamValue("vu");
      String resourceCategory = wrapper.getParamValue("resourceCategory");
      String parentId = wrapper.getParamValue("parentId");
      if (null == validateUniqueFieldJson) {
         return new RestValidationResultBean(true);
      } else {
         Map<String, Object> validateUniqueFieldMap = JSONObject.parseObject(
            validateUniqueFieldJson, new TypeReference<Map<String, Object>>() {}, new Feature[0]
         );
         Map<String, Object> columnMap = new CaseInsensitiveLinkedMap<>(validateUniqueFieldMap);
         Long id = ConvertUtils.convert(columnMap.get("id"), Long.class);
         columnMap.remove("id");
         if (StringUtils.isEmpty(resourceCategory) || StringUtils.isEmpty(parentId)) {
            return this.validateUnique(id, columnMap);
         } else if (StringUtils.equals("mapper", resourceCategory)) {
            List<String> mapperNameList = new ArrayList<>();
            LcdpConstant.MAPPER_TMPL_NAME_LIST.forEach(mapperName -> mapperNameList.add(columnMap.get("resourceName").toString() + mapperName));
            List<LcdpResourceBean> mapperResourceList = this.selectListByFilter(
               SearchFilter.instance()
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ)
                  .match("PARENTID", Long.valueOf(parentId))
                  .filter(MatchPattern.EQ)
                  .match("RESOURCENAME", mapperNameList)
                  .filter(MatchPattern.OR),
               new Order[0]
            );
            return !mapperResourceList.isEmpty()
               ? new RestValidationResultBean(false, I18nHelper.getMessage("core.validator.column.not-unique"))
               : new RestValidationResultBean(true);
         } else {
            List<LcdpResourceBean> resourceList = this.selectListByFilter(
               SearchFilter.instance()
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ)
                  .match("RESOURCENAME", columnMap.get("resourceName").toString())
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
            String pathStr = null;
            if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resourceCategory)) {
               pathStr = resourceList.stream()
                  .filter(resource -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resource.getResourceCategory()))
                  .map(LcdpResourceBean::getPath)
                  .collect(Collectors.joining(";"));
               resourceList = resourceList.stream()
                  .filter(re -> ObjectUtils.equals(re.getParentId(), Long.valueOf(parentId)) && StringUtils.equals(re.getResourceCategory(), resourceCategory))
                  .collect(Collectors.toList());
            }

            String resourceId = wrapper.getParamValue("resourceId");
            if (!StringUtils.isEmpty(resourceId)) {
               boolean flag = resourceList.stream().allMatch(e -> ObjectUtils.equals(e.getId(), Long.valueOf(resourceId)));
               if (flag) {
                  return new RestValidationResultBean(true, StringUtils.isEmpty(pathStr) ? null : pathStr);
               }
            }

            return !resourceList.isEmpty()
               ? new RestValidationResultBean(false, I18nHelper.getMessage("core.validator.column.not-unique"))
               : new RestValidationResultBean(true, StringUtils.isEmpty(pathStr) ? null : pathStr);
         }
      }
   }

   public RestValidationResultBean validateUnique(Long id, Map<String, Object> columnMap) {
      if (!ObjectUtils.isEmpty(columnMap.get("resourceName"))) {
         String resourceName = columnMap.get("resourceName").toString();
         List<LcdpResourceBean> resourceList = this.selectListByFilter(
            SearchFilter.instance().match("RESOURCENAME", resourceName).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
         );
         return !resourceList.isEmpty()
            ? new RestValidationResultBean(false, I18nHelper.getMessage("core.validator.column.not-unique"))
            : new RestValidationResultBean(true);
      } else {
         return LcdpResourceService.super.validateUnique(id, columnMap);
      }
   }

   @Override
   public List<LcdpResourceTreeNodeDTO> selectResourceTree(RestJsonWrapperBean wrapper) {
      Map<String, String> paramMap = wrapper.getParamMap();
      String resourceName = paramMap.get("resourceName");
      List<LcdpResourceBean> resultResourceList = new ArrayList<>();
      List<LcdpResourceBean> resourceList = this.selectListByFilter(
         SearchFilter.instance().match("RESOURCENAME", resourceName.toUpperCase()).filter(MatchPattern.CISC).match("DELETEFLAG", "0").filter(MatchPattern.EQ),
         new Order[0]
      );
      resourceList.addAll(
         this.selectListByFilter(
            SearchFilter.instance()
               .match("RESOURCEDESC", resourceName.toUpperCase())
               .filter(MatchPattern.CISC)
               .match("DELETEFLAG", "0")
               .filter(MatchPattern.EQ),
            new Order[0]
         )
      );
      resourceList = resourceList.stream()
         .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(tc -> tc.getId()))), ArrayList::new));
      if (resourceList.isEmpty()) {
         return new ArrayList<>();
      } else {
         List<LcdpResourceBean> categoryList = resourceList.stream()
            .filter(resource -> "category".equals(resource.getResourceCategory()))
            .collect(Collectors.toList());
         if (!ObjectUtils.isEmpty(categoryList)) {
            List<Long> categoryIdList = categoryList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
            resultResourceList.addAll(categoryList);
            List<LcdpResourceBean> queryModuleList = this.selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "module")
                  .filter(MatchPattern.OR)
                  .match("PARENTID", categoryIdList)
                  .filter(MatchPattern.OR)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
            if (!queryModuleList.isEmpty()) {
               resultResourceList.addAll(queryModuleList);
               this.removeResourceList(resourceList, queryModuleList);
               List<Long> moduleIdList = queryModuleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
               List<LcdpResourceBean> queryScriptList = this.selectListByFilter(
                  SearchFilter.instance().match("PARENTID", moduleIdList).filter(MatchPattern.OR).match("DELETEFLAG", "0").filter(MatchPattern.EQ),
                  new Order[0]
               );
               if (!queryScriptList.isEmpty()) {
                  this.removeResourceList(resourceList, queryScriptList);
                  queryScriptList = queryScriptList.stream().filter(resource -> !this.noReturnDataFilter(resource)).collect(Collectors.toList());
                  queryScriptList = this.dealTreeMapper(queryScriptList);
                  resultResourceList.addAll(queryScriptList);
               }
            }
         }

         List<LcdpResourceBean> moduleList = resourceList.stream()
            .filter(resource -> "module".equals(resource.getResourceCategory()))
            .collect(Collectors.toList());
         if (!moduleList.isEmpty()) {
            resultResourceList.addAll(moduleList);
            this.removeResourceList(resourceList, moduleList);
            List<Long> categoryIdList = moduleList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
            List<LcdpResourceBean> queryCategoryList = this.selectListByFilter(
               SearchFilter.instance().match("RESOURCECATEGORY", "category").filter(MatchPattern.EQ).match("ID", categoryIdList).filter(MatchPattern.OR),
               new Order[0]
            );
            resultResourceList.addAll(queryCategoryList);
            List<Long> moduleIdList = moduleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
            List<LcdpResourceBean> queryScriptList = this.selectListByFilter(
               SearchFilter.instance().match("PARENTID", moduleIdList).filter(MatchPattern.OR).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
            );
            if (!queryScriptList.isEmpty()) {
               this.removeResourceList(resourceList, queryScriptList);
               queryScriptList = queryScriptList.stream().filter(resource -> !this.noReturnDataFilter(resource)).collect(Collectors.toList());
               queryScriptList = this.dealTreeMapper(queryScriptList);
               resultResourceList.addAll(queryScriptList);
            }
         }

         List<LcdpResourceBean> scriptList = resourceList.stream()
            .filter(resource -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resource.getResourceCategory()))
            .collect(Collectors.toList());
         scriptList = scriptList.stream()
            .filter(resource -> !this.noReturnDataFilter(resource))
            .filter(resouce -> !StringUtils.equals("FileScript", resouce.getResourceName()))
            .collect(Collectors.toList());
         scriptList = this.dealTreeMapper(scriptList);
         this.dealResourceName(scriptList);
         if (!scriptList.isEmpty()) {
            resultResourceList.addAll(scriptList);
            List<Long> moduleIdList = scriptList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
            List<LcdpResourceBean> queryModuleList = this.selectListByFilter(
               SearchFilter.instance().match("RESOURCECATEGORY", "module").filter(MatchPattern.EQ).match("ID", moduleIdList).filter(MatchPattern.OR),
               new Order[0]
            );
            resultResourceList.addAll(queryModuleList);
            List<Long> categoryIdList = queryModuleList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
            List<LcdpResourceBean> queryCategoryList = this.selectListByFilter(
               SearchFilter.instance().match("RESOURCECATEGORY", "category").filter(MatchPattern.EQ).match("ID", categoryIdList).filter(MatchPattern.OR),
               new Order[0]
            );
            this.removeResourceList(resultResourceList, queryCategoryList);
            resultResourceList.addAll(queryCategoryList);
         }

         List<String> scriptIdList = resultResourceList.stream()
            .filter(resource -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resource.getResourceCategory()))
            .map(LcdpResourceBean::getId)
            .map(String::valueOf)
            .collect(Collectors.toList());
         List<LcdpResourceLockBean> lockedResourceList = this.lcdpResourceLockService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("LOCKUSERID", LocalContextHelper.getLoginUserId())
                  .filter(MatchPattern.EQ)
                  .match("RESOURCEID", scriptIdList)
                  .filter(MatchPattern.OR),
               new Order[0]
            );
         Map<String, String> resourceLockStatusMap = lockedResourceList.stream()
            .collect(Collectors.toMap(LcdpResourceLockBean::getResourceId, LcdpResourceLockBean::getLockUserId, (v1, v2) -> v1));
         resultResourceList.stream()
            .filter(resource -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resource.getResourceCategory()))
            .forEach(resource -> resource.setExt$Item("status", resourceLockStatusMap.containsKey(resource.getId().toString()) ? "1" : "0"));
         resultResourceList = resultResourceList.stream()
            .sorted(Comparator.comparing(LcdpResourceBean::getResourceName, Comparator.nullsFirst(String::compareTo)))
            .collect(Collectors.toList());

         for (int i = 0; i < resultResourceList.size(); i++) {
            LcdpResourceBean resource = resultResourceList.get(i);
            if ("module".equals(resource.getResourceCategory())) {
               this.bulidFolder(resultResourceList, resource);
            }
         }

         TreeDescriptor<LcdpResourceBean> descriptor = new TreeDescriptor<>("id", "parentId", "resourceName", "orderNo");
         descriptor.setParseTreeNodeFunction(t -> {
            LcdpResourceTreeNodeDTO resourceDTO = LcdpResourceTreeNodeDTO.of(t);
            resourceDTO.setCheckOutFlag(t.getExt$Item("status"));
            if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(t.getResourceCategory())) {
               resourceDTO.setResourceStatus(null == t.getEffectVersion() ? "new" : "valid");
            }

            return resourceDTO;
         });
         List<LcdpResourceTreeNodeDTO> resourceDTOList = TreeHelper.parseTreeNode(resultResourceList, descriptor, LcdpResourceTreeNodeDTO.class);
         TreeHelper.updateChildQty(resourceDTOList);
         return resourceDTOList;
      }
   }

   @Override
   public LcdpWorkspaceDTO selectWorkSpaceData(RestJsonWrapperBean wrapper) {
      LcdpWorkspaceDTO workspaceDTO = new LcdpWorkspaceDTO();
      List<LcdpResourceBean> moduleList = this.selectListByFilter(
         SearchFilter.instance().match("RESOURCECATEGORY", "module").filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
      );
      List<String> moduleIdList = moduleList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
      workspaceDTO.setModuleIdList(moduleIdList);
      return workspaceDTO;
   }

   @Override
   public String replaceScriptContent(String content) {
      Pattern autowiredClassPattern = Pattern.compile("@Autowired\\s*(\\r|\\n)*\\s*private\\s+(?<Class>\\w+)\\s+(?<fieldName>\\w+);");
      Matcher matcher = autowiredClassPattern.matcher(content);

      while (matcher.find()) {
         String autowiredClass = matcher.group("Class");
         String autowiredFieldName = matcher.group("fieldName");
         Class<?> autowiredClazz = ClassUtils.getGikamClass(autowiredClass);
         if (autowiredClazz == null) {
            Class<?> aClass = DynamicClassLoader.getClass(autowiredClass);
            List<Entry<String, Class<?>>> classList = DynamicClassLoader.getClassList(autowiredClass);
            List<String> classNameList = classList.stream().map(Entry::getKey).collect(Collectors.toList());
            if (!classNameList.isEmpty()) {
               Pattern vPattern = Pattern.compile("v(\\d+)");
               Pattern mPattern = Pattern.compile("m(\\d+)");
               String startClassName = classNameList.get(0).substring(0, classNameList.get(0).indexOf(autowiredClass) + autowiredClass.length());
               List<Integer> versionList = classNameList.stream().map(classNamex -> {
                  Matcher vMatcher = vPattern.matcher(classNamex);
                  return vMatcher.find() ? Integer.valueOf(vMatcher.group(1)) : 0;
               }).collect(Collectors.toList());
               Integer maxVersion = Collections.max(versionList);
               List<Integer> modifyVersionList = classNameList.stream()
                  .filter(classNamex -> StringUtils.contains(classNamex, autowiredClass + "v" + maxVersion))
                  .map(classNamex -> {
                     Matcher mMatcher = mPattern.matcher(classNamex);
                     return mMatcher.find() ? Integer.valueOf(mMatcher.group(1)) : 0;
                  })
                  .collect(Collectors.toList());
               Integer maxModifyVersion = Collections.max(modifyVersionList);
               String className = "";
               String vClassName = "";
               if (modifyVersionList.stream().anyMatch(m -> m == 0)) {
                  className = startClassName + "v" + maxVersion;
                  vClassName = startClassName + "v" + maxVersion;
               } else {
                  className = startClassName + "v" + maxVersion + "m" + maxModifyVersion;
               }

               Pattern pattern = Pattern.compile(".*m\\d+$");
               Matcher classNameMatcher = pattern.matcher(className);
               if (!classNameMatcher.matches()) {
                  aClass = DynamicClassLoader.getUseClass(className);
               } else if (LcdpScriptUtils.isDebugCall()) {
                  LcdpResourceHistoryBean filter = new LcdpResourceHistoryBean();
                  filter.setResourceName(autowiredClass);
                  filter.setCreatedById(LocalContextHelper.getLoginUserId());
                  filter.setSubmitFlag("no");
                  int count = this.lcdpResourceHistoryService.getDao().countBy(filter);
                  if (count == 0) {
                     aClass = DynamicClassLoader.getUseClass(vClassName);
                  } else {
                     aClass = DynamicClassLoader.getUseClass(className);
                  }
               } else {
                  aClass = DynamicClassLoader.getUseClass(vClassName);
               }
            }

            if (aClass != null) {
               String getMethodName = "get" + StringUtils.capitalize(autowiredFieldName);
               content = content.replaceFirst(
                  matcher.group(), "private Object " + getMethodName + "() {\n return LcdpScriptUtils.getBean(\"" + autowiredClass + "\");}"
               );
               Pattern autowiredFieldMethodPatternWithEqualSign = Pattern.compile("=\\s*" + autowiredFieldName + "\\.(?<methodName>[A-Za-z0-9]+)\\(");
               Matcher autowiredFieldMethodMatcherWithEqualSign = autowiredFieldMethodPatternWithEqualSign.matcher(content);

               while (autowiredFieldMethodMatcherWithEqualSign.find()) {
                  String methodName = autowiredFieldMethodMatcherWithEqualSign.group("methodName");
                  MethodWrapper methodWrapper = ReflectionUtils.getMethodWrapperListByName(aClass, methodName).stream().findFirst().orElse(null);
                  String returnTypeName = methodWrapper != null ? methodWrapper.getReturnType().getTypeName() : "Object";
                  content = StringUtils.replace(
                     content,
                     autowiredFieldMethodMatcherWithEqualSign.group(),
                     " = (" + returnTypeName + ") SpringCaller.of(" + getMethodName + "(), \"" + methodName + "\").call("
                  );
               }

               Pattern autowiredFieldMethodPatternWithReturn = Pattern.compile("return\\s*" + autowiredFieldName + "\\.(?<methodName>[A-Za-z0-9]+)\\(");
               Matcher autowiredFieldMethodMatcherWithReturn = autowiredFieldMethodPatternWithReturn.matcher(content);

               while (autowiredFieldMethodMatcherWithReturn.find()) {
                  String methodName = autowiredFieldMethodMatcherWithReturn.group("methodName");
                  MethodWrapper methodWrapper = ReflectionUtils.getMethodWrapperListByName(aClass, methodName).stream().findFirst().orElse(null);
                  String returnTypeName = methodWrapper != null ? methodWrapper.getReturnType().getTypeName() : "Object";
                  content = StringUtils.replace(
                     content,
                     autowiredFieldMethodMatcherWithReturn.group(),
                     " return (" + returnTypeName + ") SpringCaller.of(" + getMethodName + "(), \"" + methodName + "\").call("
                  );
               }

               Pattern autowiredFieldMethodPattern = Pattern.compile("\\x20{1}" + autowiredFieldName + "\\.(?<methodName>[A-Za-z0-9]+)\\(");
               Matcher autowiredFieldMethodMatcher = autowiredFieldMethodPattern.matcher(content);

               while (autowiredFieldMethodMatcher.find()) {
                  String methodName = autowiredFieldMethodMatcher.group("methodName");
                  content = StringUtils.replace(
                     content, autowiredFieldMethodMatcher.group(), " SpringCaller.of(" + getMethodName + "(), \"" + methodName + "\").call("
                  );
               }
            }
         }
      }

      Pattern utilsPattern = Pattern.compile("Lcdp\\w+Utils");
      Matcher utilsPatternMatcher = utilsPattern.matcher(content);
      List<String> replacedUtilsNameList = new ArrayList<>();

      while (utilsPatternMatcher.find()) {
         boolean isLcdpUtilsClass = DynamicClassLoader.isLcdpUtilsClass(utilsPatternMatcher.group());
         if (!isLcdpUtilsClass) {
            String classNamex = JavaCompilerManager.getClassName(content);
            String group = utilsPatternMatcher.group();
            if (!StringUtils.contains(classNamex, group + "v") && !replacedUtilsNameList.contains(group)) {
               Pattern utilsMethodWithEqualSignPattern = Pattern.compile("=\\s*" + group + "\\.(?<methodName>[A-Za-z0-9]+)\\(");
               Matcher utilsMethodWithEqualSignMatcher = utilsMethodWithEqualSignPattern.matcher(content);

               while (utilsMethodWithEqualSignMatcher.find()) {
                  String methodName = utilsMethodWithEqualSignMatcher.group("methodName");
                  String utilsClassName = LcdpScriptUtils.getUtilsClassName(group);
                  DynamicClassLoader.getInstance();
                  Class utilClass = DynamicClassLoader.getClassByClassName(utilsClassName);
                  MethodWrapper methodWrapper = ReflectionUtils.getMethodWrapperListByName(utilClass, methodName).stream().findFirst().orElse(null);
                  String returnTypeName = methodWrapper != null ? methodWrapper.getReturnType().getTypeName() : "Object";
                  content = StringUtils.replace(
                     content,
                     utilsMethodWithEqualSignMatcher.group(),
                     " = (" + returnTypeName + ")UtilsCaller.of(\"" + group + "\",\"" + methodName + "\").call("
                  );
               }

               Pattern utilsMethodPatternWithReturn = Pattern.compile("return\\s*" + group + "\\.(?<methodName>[A-Za-z0-9]+)\\(");
               Matcher utilsMethodMatcherWithReturn = utilsMethodPatternWithReturn.matcher(content);

               while (utilsMethodMatcherWithReturn.find()) {
                  String methodName = utilsMethodMatcherWithReturn.group("methodName");
                  String utilsClassName = LcdpScriptUtils.getUtilsClassName(group);
                  DynamicClassLoader.getInstance();
                  Class utilClass = DynamicClassLoader.getClassByClassName(utilsClassName);
                  MethodWrapper methodWrapper = ReflectionUtils.getMethodWrapperListByName(utilClass, methodName).stream().findFirst().orElse(null);
                  String returnTypeName = methodWrapper != null ? methodWrapper.getReturnType().getTypeName() : "Object";
                  content = StringUtils.replace(
                     content,
                     utilsMethodMatcherWithReturn.group(),
                     " return (" + returnTypeName + ")UtilsCaller.of(\"" + group + "\",\"" + methodName + "\").call("
                  );
               }

               Pattern utilsMethodPattern = Pattern.compile(group + "\\.(?<methodName>[A-Za-z0-9]+)\\(");
               Matcher utilsMethodMatcher = utilsMethodPattern.matcher(content);

               while (utilsMethodMatcher.find()) {
                  String methodName = utilsMethodMatcher.group("methodName");
                  content = StringUtils.replace(content, utilsMethodMatcher.group(), "UtilsCaller.of(\"" + group + "\",\"" + methodName + "\").call(");
               }

               String utilsClassName = LcdpScriptUtils.getUtilsClassName(group);
               DynamicClassLoader.getInstance();
               Class utilClass = DynamicClassLoader.getClassByClassName(utilsClassName);
               if (utilClass != null) {
                  for (Field field : ReflectionUtils.getFiledList(utilClass)) {
                     Pattern utilsConstantPattern = Pattern.compile(group + "." + field.getName());
                     Matcher utilsConstantMatcher = utilsConstantPattern.matcher(content);

                     while (utilsConstantMatcher.find()) {
                        content = StringUtils.replace(
                           content,
                           utilsConstantMatcher.group(),
                           "(" + field.getType().getTypeName() + ")UtilsCaller.of(\"" + group + "\",\"" + field.getName() + "\").getConstant()"
                        );
                     }
                  }
               }

               replacedUtilsNameList.add(group);
            }
         }
      }

      return content;
   }

   @Override
   public LcdpResultDTO compileJavaCodes(RestJsonWrapperBean wrapper) {
      try {
         String classContent = this.replaceScriptContent(wrapper.getParamValue("content"));
         JavaCompilerManager.compile(classContent);
      } catch (CompileException var3) {
         return new LcdpResultDTO("200", JSONObject.toJSON(var3.getMessage()).toString());
      }

      return new LcdpResultDTO("200", null);
   }

   @Override
   public List<String> generatePackageImport(RestJsonWrapperBean wrapper) {
      List<String> list = new ArrayList<>();
      this.getPackageImport(wrapper.getParamValue("content"), list);
      return list;
   }

   @Override
   public String getJavaCodeSource(String className) {
      LcdpResourceDTO resource = this.selectScriptContent(className);
      return resource != null ? resource.getContent() : this.decompileClassBySimpleName(className);
   }

   @Override
   public LcdpResourceDTO selectResourceByClassName(RestJsonWrapperBean wrapper) {
      String className = wrapper.getParamValue("simpleClassName");
      LcdpResourceDTO resource = this.selectScriptContent(className);
      if (resource != null) {
         return resource;
      } else {
         String content = this.decompileClassBySimpleName(className);
         LcdpResourceDTO resourceDTO = new LcdpResourceDTO();
         resourceDTO.setType("java");
         resourceDTO.setContent(content);
         resourceDTO.setName(className);
         return resourceDTO;
      }
   }

   private boolean validateScriptCallable(String scriptPath) {
      if (StringUtils.isEmpty(scriptPath)) {
         throw new CheckedException("GIKAM.LCDP.SCRIPT_PATH_REQUIRED");
      }
      if (scriptPath.indexOf("lcdp.file.server.FileScript") >= 0) {
         return true;
      }
      String path = LcdpScriptPath.methodPath(scriptPath);
      List<LcdpResourceHistoryBean> existResourceList = this.lcdpResourceHistoryService
         .getDao()
         .selectListByOneColumnValue(path, "PATH", ArrayUtils.asList("DELETEFLAG"));
      if (existResourceList.isEmpty()) {
         throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.METHOD_NOT_EXITS") + ":" + scriptPath);
      }
      List<LcdpResourceHistoryBean> resourceList = existResourceList.stream()
         .filter(re -> StringUtils.equals("0", re.getDeleteFlag()))
         .collect(Collectors.toList());
      if (resourceList.isEmpty()) {
         throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.HAS_BEEN_DELETED") + ":" + scriptPath);
      }
      return true;
   }

   private void checkAccessPermission(String scriptPath) {
      List<CoreRequestUrlBean> requestUrlList = ApplicationContextHelper.getBean(CoreRequestUrlService.class).selectListByRequestUrl(scriptPath);
      if (!CollectionUtils.isEmpty(requestUrlList)) {
         List<CoreMenuBean> currentUserMenuList = this.coreMenuService.selectCurrentUserMenuList();
         if (requestUrlList.stream().noneMatch(u -> currentUserMenuList.stream().anyMatch(m -> m.getId().equals(u.getMenuId())))) {
            throw new ApplicationWarningException("GIKAM.PERMISSION.UNAUTHORIZED");
         }
      }
   }

   private void initAccessPermission(String scriptPath) {
      HttpServletRequest currentRequest = ServletUtils.getCurrentRequest();
      String menuId = currentRequest.getHeader("sunway-menu");
      if (!StringUtils.isBlank(menuId)) {
         List<String> searchColNames = Arrays.asList("menuId", "url");
         List<String> selectColNames = Arrays.asList("id");
         CoreRequestUrlBean urlBean = new CoreRequestUrlBean();
         urlBean.setMenuId(menuId);
         urlBean.setNote("auto insert");
         urlBean.setUrl(scriptPath);
         GikamConcurrentLocker.blockRun(
            "AUTO_INSERT_ACCESS::" + menuId + scriptPath,
            () -> {
               List<CoreRequestUrlBean> requestUrlList = ApplicationContextHelper.getBean(CoreRequestUrlService.class)
                  .getDao()
                  .selectList(urlBean, searchColNames, selectColNames);
               if (CollectionUtils.isEmpty(requestUrlList)) {
                  urlBean.setId(ApplicationContextHelper.getNextIdentity());
                  ApplicationContextHelper.getBean(CoreRequestUrlService.class).getDao().insert(urlBean);
               }
            }
         );
      }
   }

   private void removeResourceList(List<LcdpResourceBean> sourceList, List<LcdpResourceBean> toDeleteList) {
      if (!toDeleteList.isEmpty()) {
         List<Long> toDeleteIdList = toDeleteList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceBean> removeDataList = sourceList.stream().filter(resource -> toDeleteIdList.contains(resource.getId())).collect(Collectors.toList());
         if (!removeDataList.isEmpty()) {
            sourceList.removeAll(removeDataList);
         }
      }
   }

   public String getScriptByPath(String path, String callType) {
      LcdpResourceBean memoryResource = LcdpResourceCache.getMemoryContent(path);
      if (memoryResource == null) {
         LcdpResourceBean filter = new LcdpResourceBean();
         filter.setPath(path);
         filter.setDeleteFlag("0");
         memoryResource = this.getDao().selectFirstIfPresent(filter, new Order[0]);
         if (memoryResource == null) {
            LcdpResourceHistoryBean historyFilter = new LcdpResourceHistoryBean();
            historyFilter.setPath(path);
            historyFilter.setDeleteFlag("0");
            LcdpResourceHistoryBean resourceHistory = this.lcdpResourceHistoryService.getDao().selectFirst(historyFilter);
            memoryResource = this.getDao().selectById(resourceHistory.getResourceId());
         } else {
            LcdpResourceCache.put(path, memoryResource);
         }
      }

      if ("pro".equals(callType)) {
         return memoryResource.getClassContent();
      } else {
         LcdpResourceLockBean lockResource = null;
         if (StringUtils.equals("sysAdmin", LocalContextHelper.getLoginUserId())) {
            List<LcdpResourceLockBean> lockList = this.lcdpResourceLockService
               .selectListByFilter(
                  SearchFilter.instance()
                     .match("RESOURCEID", String.valueOf(memoryResource.getId()))
                     .filter(MatchPattern.EQ)
                     .match("LOCKUSERID", null)
                     .filter(MatchPattern.DIFFER),
                  new Order[0]
               );
            lockResource = lockList.stream().findFirst().orElse(null);
         } else {
            LcdpResourceLockBean lockFilter = new LcdpResourceLockBean();
            lockFilter.setResourceId(String.valueOf(memoryResource.getId()));
            lockFilter.setLockUserId(LocalContextHelper.getLoginUserId());
            lockResource = this.lcdpResourceLockService.getDao().selectFirstIfPresent(lockFilter);
         }

         if (lockResource == null) {
            return memoryResource.getClassContent();
         } else {
            LcdpResourceHistoryBean historyFilter = new LcdpResourceHistoryBean();
            historyFilter.setResourceId(memoryResource.getId());
            historyFilter.setDeleteFlag("0");
            LcdpResourceHistoryBean historyResource = this.lcdpResourceHistoryService.getDao().selectFirst(historyFilter, Order.desc("VERSION"));
            return historyResource.getClassContent();
         }
      }
   }

   private LcdpResourceDTO getDesignResourceDTO(LcdpResourceBean resource) {
      LcdpResourceHistoryBean historyCondition = new LcdpResourceHistoryBean();
      historyCondition.setResourceId(resource.getId());
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .getDao()
         .selectUnionList(
            historyCondition, Arrays.asList("ID", "RESOURCEID", "RESOURCENAME", "RESOURCECATEGORY", "CONTENT", "PATH", "SUBMITFLAG", "CREATEDBYID", "VERSION")
         );
      if (ObjectUtils.isEmpty(resource.getEffectVersion())) {
         LcdpResourceDTO resourceDTO = new LcdpResourceDTO();
         resourceDTO.setEditable(true);
         resourceDTO = this.buildLcdpResourceDTO(resourceHistoryList.get(0));
         resourceDTO.setResourceStatus("new");
         return resourceDTO;
      } else {
         new ArrayList();
         List currentUserEditResourceList;
         if (StringUtils.equals("sysAdmin", LocalContextHelper.getLoginUserId())) {
            currentUserEditResourceList = resourceHistoryList.stream().filter(re -> "no".equals(re.getSubmitFlag())).collect(Collectors.toList());
         } else {
            currentUserEditResourceList = resourceHistoryList.stream()
               .filter(re -> "no".equals(re.getSubmitFlag()) && LocalContextHelper.getLoginUserId().equals(re.getCreatedById()))
               .collect(Collectors.toList());
         }

         if (!ObjectUtils.isEmpty(currentUserEditResourceList)) {
            LcdpResourceDTO resourceDTO = this.buildLcdpResourceDTO((LcdpResourceHistoryBean)currentUserEditResourceList.get(0));
            resourceDTO.setEditable(true);
            resourceDTO.setResourceStatus("valid");
            return resourceDTO;
         } else {
            List<LcdpResourceHistoryBean> historyList = resourceHistoryList.stream()
               .filter(re -> resource.getEffectVersion().equals(re.getVersion()))
               .collect(Collectors.toList());
            LcdpResourceDTO resourceDTO = this.buildLcdpResourceDTO(historyList.get(0));
            resourceDTO.setEditable(false);
            return resourceDTO;
         }
      }
   }

   private void copyHistoryResource(Long resourceId, Long targetId, String path, String copyName, String desc) {
      Map<Long, Long> resourceIdMap = new HashMap<>();
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resourceId).filter(MatchPattern.SEQ), new Order[0]);
      List<LcdpResourceHistoryBean> sourceResourceHistoryList = resourceHistoryList.stream()
         .filter(history -> LocalContextHelper.getLoginUserId().equals(history.getCreatedById()) && "no".equals(history.getSubmitFlag()))
         .collect(Collectors.toList());
      new LcdpResourceHistoryBean();
      LcdpResourceHistoryBean resourceHistory;
      if (!ObjectUtils.isEmpty(sourceResourceHistoryList)) {
         resourceHistory = sourceResourceHistoryList.get(0);
      } else {
         resourceHistory = resourceHistoryList.stream().filter(history -> "yes".equals(history.getEffectFlag())).findFirst().get();
      }

      LcdpResourceHistoryBean targetResourceHistory = new LcdpResourceHistoryBean();
      BeanUtils.copyProperties(resourceHistory, targetResourceHistory, LcdpConstant.COPY_IGNORE_PROPERTIES);
      targetResourceHistory.setId(ApplicationContextHelper.getNextIdentity());
      targetResourceHistory.setResourceId(targetId);
      targetResourceHistory.setResourceName(copyName);
      targetResourceHistory.setResourceDesc(desc);
      targetResourceHistory.setPath(path);
      targetResourceHistory.setEffectFlag("no");
      targetResourceHistory.setSubmitFlag("no");
      targetResourceHistory.setVersion(1L);
      if (StringUtils.equals("java", targetResourceHistory.getResourceCategory())) {
         String content = targetResourceHistory.getContent();
         content = content.replaceFirst("public\\s+class\\s+" + JavaCompilerManager.getClassName(content), "public class " + copyName);
         targetResourceHistory.setContent(content);
         String classContent = this.replaceClassName(path, content, resourceHistory.getResourceName(), "v1", "m1");
         resourceHistory.setClassContent(classContent);
      }

      EntityHelper.assignCreatedElement(targetResourceHistory);
      this.lcdpResourceHistoryService.getDao().insert(targetResourceHistory);
      resourceIdMap.put(resourceHistory.getId(), targetResourceHistory.getId());
      if ("view".equals(targetResourceHistory.getResourceCategory())) {
         Map<String, String> pathMap = new HashMap<>();
         pathMap.put(resourceHistory.getPath(), targetResourceHistory.getPath());
         this.copyPageComps(Arrays.asList(resourceHistory), resourceIdMap, pathMap);
      }

      if ("java".equals(targetResourceHistory.getResourceCategory())) {
         this.insertServerScriptMethod(targetResourceHistory);
      }

      LuceneIndexManager.addIndex(new LcdpResourceDocumentBean(targetResourceHistory));
      this.operateCompDocument(null, targetResourceHistory, "add");
   }

   private void copyChildren(List<LcdpResourceBean> childrenList, LcdpResourceBean target, String sourceModuleName) {
      Map<Long, Long> resourceIdMap = new HashMap<>();
      Map<Long, String> viewNameMap = new HashMap<>();
      List<LcdpResourceBean> newChildrenList = childrenList.stream().map(old -> {
         LcdpResourceBean newChildren = new LcdpResourceBean();
         BeanUtils.copyProperties(old, newChildren, "effectVersion");
         newChildren.setId(ApplicationContextHelper.getNextIdentity());
         newChildren.setParentId(target.getId());
         newChildren.setEffectVersion(null);
         newChildren.setResourceName(old.getResourceName());
         if (StringUtils.equals("view", old.getResourceCategory())) {
            newChildren.setResourceName(newChildren.getResourceName().replaceFirst(sourceModuleName, target.getResourceName()));
            viewNameMap.put(newChildren.getId(), newChildren.getResourceName());
         }

         EntityHelper.assignCreatedElement(newChildren);
         resourceIdMap.put(old.getId(), newChildren.getId());
         return newChildren;
      }).collect(Collectors.toList());
      this.generateFilePath(newChildrenList);
      this.getDao().insert(newChildrenList);
      Map<Long, String> resourcePathMap = newChildrenList.stream()
         .collect(Collectors.toMap(LcdpResourceBean::getId, LcdpResourceBean::getPath, (v1, v2) -> v2));
      List<LcdpResourceHistoryBean> requiredList = new ArrayList<>();
      List<Long> childrenIdList = childrenList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      List<LcdpResourceHistoryBean> historyList = this.lcdpResourceHistoryService
         .selectListByFilter(
            SearchFilter.instance().match("resourceId", childrenIdList).filter(MatchPattern.OR).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
         );
      List<LcdpResourceHistoryBean> editList = historyList.stream()
         .filter(his -> "no".equals(his.getSubmitFlag()) && LocalContextHelper.getLoginUserId().equals(his.getCreatedById()))
         .collect(Collectors.toList());
      List<LcdpResourceHistoryBean> effectList = historyList.stream().filter(his -> "yes".equals(his.getEffectFlag())).collect(Collectors.toList());
      List<Long> editIdList = editList.stream().map(LcdpResourceHistoryBean::getResourceId).collect(Collectors.toList());
      requiredList.addAll(editList);

      for (int i = 0; i < effectList.size(); i++) {
         LcdpResourceHistoryBean effectData = effectList.get(i);
         if (!editIdList.contains(effectData.getResourceId())) {
            requiredList.add(effectData);
         }
      }

      Map<Long, Long> historyIdMap = new HashMap<>();
      Map<String, String> pathMap = new HashMap<>();
      Map<String, String> scriptPathMap = new HashMap<>();
      List<LcdpResourceHistoryBean> insertList = requiredList.stream()
         .map(
            r -> {
               LcdpResourceHistoryBean insertResource = new LcdpResourceHistoryBean();
               BeanUtils.copyProperties(r, insertResource, LcdpConstant.COPY_IGNORE_PROPERTIES);
               insertResource.setId(ApplicationContextHelper.getNextIdentity());
               insertResource.setResourceId(resourceIdMap.get(r.getResourceId()));
               insertResource.setEffectFlag("no");
               insertResource.setSubmitFlag("no");
               insertResource.setVersion(1L);
               insertResource.setPath(resourcePathMap.get(resourceIdMap.get(r.getResourceId())));
               insertResource.setModifyVersion(1);
               if ("mapper".equals(r.getResourceCategory())) {
                  String newNameSpace = null;
                  String sourceNameSpace = null;

                  for (String suffixName : LcdpConstant.MAPPER_TMPL_NAME_LIST) {
                     if (insertResource.getResourceName().endsWith(suffixName)) {
                        newNameSpace = StringUtils.replaceLast(insertResource.getPath(), suffixName, "") + "Mapper";
                        sourceNameSpace = StringUtils.replaceLast(r.getPath(), suffixName, "") + "Mapper";
                     }
                  }

                  insertResource.setContent(r.getContent().replace(sourceNameSpace, newNameSpace));
                  LcdpMapperUtils.loadMapper(insertResource.getPath(), false, insertResource.getContent());
                  LcdpMapperUtils.syncMapper(insertResource.getPath(), false, insertResource.getContent());
               }

               if ("java".equals(r.getResourceCategory())) {
                  insertResource.setContent(
                     r.getContent().replaceFirst("public\\s+class\\s+" + r.getResourceName(), "public class " + insertResource.getResourceName())
                  );
                  insertResource.setContent(
                     insertResource.getContent()
                        .replace(
                           r.getPath().replace("server.", "mapper.").replace("Service", "Mapper"),
                           insertResource.getPath().replace("server.", "mapper.").replace("Service", "Mapper")
                        )
                  );
                  scriptPathMap.put(r.getPath(), insertResource.getPath());
                  String content = insertResource.getContent();
                  String classContent = this.replaceClassName(insertResource.getPath(), content, insertResource.getResourceName(), "v1", "m1");
                  insertResource.setClassContent(classContent);
               }

               if (StringUtils.equals("view", r.getResourceCategory())) {
                  insertResource.setResourceName(viewNameMap.get(insertResource.getResourceId()));
               }

               historyIdMap.put(r.getId(), insertResource.getId());
               pathMap.put(r.getPath(), insertResource.getPath());
               return insertResource;
            }
         )
         .collect(Collectors.toList());
      insertList.stream().filter(resource -> "view".equals(resource.getResourceCategory())).forEach(resource -> scriptPathMap.forEach((k, v) -> {
         String content = resource.getContent();
         if (null != content) {
            String replaceContent = content.replaceAll(k, v);
            resource.setContent(replaceContent);
         }
      }));
      this.lcdpResourceHistoryService.getDao().insert(insertList);
      List<LcdpResourceHistoryBean> pageList = requiredList.stream().filter(re -> "view".equals(re.getResourceCategory())).collect(Collectors.toList());
      if (!ObjectUtils.isEmpty(pageList)) {
         List<LcdpModulePageCompBean> pageCompList = this.copyPageComps(pageList, historyIdMap, pathMap);
         List<LcdpModulePageCompBean> needReplaceComp = pageCompList.stream()
            .filter(comp -> LcdpConstant.RESOURCE_COPY_COMP_TYPE_LIST.contains(comp.getType()))
            .collect(Collectors.toList());
         needReplaceComp.forEach(page -> scriptPathMap.forEach((k, v) -> {
            String config = page.getConfig() + (StringUtils.isEmpty(page.getConfigExtend()) ? "" : page.getConfigExtend());
            String replaceConfig = config.replaceAll(k, v);
            this.cutConfig(replaceConfig, page);
         }));
         this.lcdpModulePageCompService.getDao().update(needReplaceComp, "CONFIG", "CONFIGEXTEND");
      }

      this.lockResource(newChildrenList);
      List<LcdpResourceHistoryBean> javaResourceHistoryList = insertList.stream()
         .filter(his -> "java".equals(his.getResourceCategory()))
         .collect(Collectors.toList());
      if (!javaResourceHistoryList.isEmpty()) {
         this.insertServerScriptMethod(javaResourceHistoryList);
      }

      List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
      newChildrenList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
      insertList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
      this.addCompDocument(newChildrenList, insertList, documentList);
      LuceneIndexManager.addIndex(documentList);
   }

   private List<LcdpModulePageCompBean> copyPageComps(List<LcdpResourceHistoryBean> pageList, Map<Long, Long> resourceIdMap, Map<String, String> pathMap) {
      List<Long> pageHistroyIdList = pageList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
      List<LcdpModulePageCompBean> modulePageCompList = this.lcdpModulePageCompService
         .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", pageHistroyIdList).filter(MatchPattern.OR), new Order[0]);
      Map<Long, String> histroyId2PathMap = pageList.stream()
         .collect(Collectors.toMap(LcdpResourceHistoryBean::getId, LcdpResourceHistoryBean::getPath, (v1, v2) -> v2));
      List<LcdpModulePageI18nBean> modulePageI18nList = this.lcdpModulePageI18nService
         .selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", pageHistroyIdList).filter(MatchPattern.OR), new Order[0]);
      Map<Long, List<LcdpModulePageI18nBean>> modulePageHistoryId2I18nListMap = modulePageI18nList.stream()
         .collect(Collectors.groupingBy(LcdpModulePageI18nBean::getModulePageHistoryId));
      List<LcdpModulePageI18nBean> insertI118nList = new ArrayList<>();
      modulePageCompList.forEach(
         comp -> {
            List<LcdpModulePageI18nBean> currentModulePageI18nList = modulePageHistoryId2I18nListMap.get(comp.getModulePageHistoryId());
            if (!ObjectUtils.isEmpty(currentModulePageI18nList)) {
               Map<String, List<LcdpModulePageI18nBean>> code2I18nMap = currentModulePageI18nList.stream()
                  .collect(Collectors.groupingBy(LcdpModulePageI18nBean::getCode));
               String config = StringUtils.isEmpty(comp.getConfigExtend()) ? comp.getConfig() : comp.getConfig() + comp.getConfigExtend();
               JSONObject configObject = JSON.parseObject(config);
               configObject.forEach(
                  (k, v) -> {
                     if (v instanceof JSONObject) {
                        JSONObject propertyObject = (JSONObject)v;

                        for (String property : propertyObject.keySet()) {
                           Object value = propertyObject.get(property);
                           if (StringUtils.equals(property, "type")
                              && StringUtils.equals(propertyObject.getString(property), "i18n")
                              && !StringUtils.isEmpty(propertyObject.getString("i18nCode"))
                              && propertyObject.getString("i18nCode").startsWith(histroyId2PathMap.get(comp.getModulePageHistoryId()))) {
                              String oldI18nCode = propertyObject.getString("i18nCode");
                              String newI18nCode = oldI18nCode.replaceAll(
                                 histroyId2PathMap.get(comp.getModulePageHistoryId()), pathMap.get(histroyId2PathMap.get(comp.getModulePageHistoryId()))
                              );
                              propertyObject.put("i18nCode", newI18nCode);
                              List<LcdpModulePageI18nBean> modulePageI18nBeanList = code2I18nMap.get(oldI18nCode);
                              if (!ObjectUtils.isEmpty(modulePageI18nBeanList)) {
                                 modulePageI18nBeanList.forEach(I18n -> {
                                    LcdpModulePageI18nBean insertModulePageI18Code = new LcdpModulePageI18nBean();
                                    BeanUtils.copyProperties(I18n, insertModulePageI18Code, LcdpConstant.COPY_IGNORE_PROPERTIES);
                                    insertModulePageI18Code.setId(ApplicationContextHelper.getNextIdentity());
                                    insertModulePageI18Code.setCode(newI18nCode);
                                    insertI118nList.add(insertModulePageI18Code);
                                 });
                                 break;
                              }
                           }
                        }
                     }
                  }
               );
               this.cutConfig(configObject.toJSONString(), comp);
            }
         }
      );
      List<LcdpModulePageCompBean> targetPageCompList = this.generatePageComps(modulePageCompList);
      this.perfectPageComps(modulePageCompList, resourceIdMap, insertI118nList);
      this.lcdpModulePageCompService.getDao().insert(targetPageCompList);
      this.lcdpModulePageI18nService.getDao().insert(insertI118nList);
      List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
      modulePageCompList.stream()
         .forEach(
            e -> {
               LcdpResourceHistoryBean resource = pageList.stream()
                  .filter(f -> ObjectUtils.equals(f.getId(), e.getModulePageHistoryId()))
                  .findFirst()
                  .orElse(new LcdpResourceHistoryBean());
               String content = e.getConfig() + (StringUtils.isEmpty(e.getConfigExtend()) ? "" : e.getConfigExtend());
               documentList.add(
                  new LcdpResourceDocumentBean(
                     resource.getId() + "$$" + e.getId(),
                     resource.getResourceName(),
                     resource.getPath(),
                     "comp",
                     content,
                     e.getModulePageVersion(),
                     "1",
                     resource.getCreatedById()
                  )
               );
            }
         );
      LuceneIndexManager.addIndex(documentList);
      return targetPageCompList;
   }

   private void updateChildrenComp(
      Map<String, String> idMap,
      List<LcdpModulePageCompBean> targetPageCompList,
      Map<String, List<LcdpModulePageCompBean>> parentId2PageCompListMap,
      List<LcdpModulePageCompBean> pageCompList
   ) {
      List<LcdpModulePageCompBean> pageCompDataList = pageCompList.stream().map(page -> {
         LcdpModulePageCompBean pageComp = new LcdpModulePageCompBean();
         BeanUtils.copyProperties(page, pageComp);
         return pageComp;
      }).collect(Collectors.toList());
      Set<String> parentIdSet = idMap.keySet();
      List<String> parentIdList = new ArrayList<>(parentIdSet);
      List<String> childrenIdList = new ArrayList<>();
      List<LcdpModulePageCompBean> parentCompList = pageCompList.stream().filter(comp -> null != idMap.get(comp.getId())).collect(Collectors.toList());
      List<LcdpModulePageCompBean> childrenCompList = new ArrayList<>();

      for (String parentId : parentIdList) {
         List<LcdpModulePageCompBean> pageCompBeanList = parentId2PageCompListMap.get(parentId);
         if (null != pageCompBeanList) {
            childrenCompList.addAll(pageCompBeanList);
         }
      }

      Map<String, LcdpModulePageCompBean> parentCompMap = parentCompList.stream().collect(Collectors.toMap(LcdpModulePageCompBean::getId, Function.identity()));
      Map<String, List<LcdpModulePageCompBean>> compListByParentIdMap = childrenCompList.stream()
         .collect(Collectors.groupingBy(LcdpModulePageCompBean::getParentId));
      Map<String, String> id2Map = new HashMap<>();
      compListByParentIdMap.forEach((parentIdx, childrenList) -> {
         LcdpModulePageCompBean parentComp = parentCompMap.get(parentIdx);
         JSONObject config = JSON.parseObject(parentComp.getConfig() + (StringUtils.isEmpty(parentComp.getConfigExtend()) ? "" : parentComp.getConfigExtend()));
         childrenList.forEach(child -> {
            String id = StringUtils.randomUUID();
            id2Map.put(child.getId(), id);
            childrenIdList.add(child.getId());
            child.setId(id);
            child.setParentId(idMap.get(parentComp.getId()));
         });
         Set<String> keySet = config.keySet();

         for (String key : keySet.stream().filter(keyx -> keyx.equals("childrenWidgetId") || keyx.endsWith("_childrenWidgetId")).collect(Collectors.toList())) {
            JSONArray childrenWidgetId = (JSONArray)config.get(key);
            if (!ObjectUtils.isEmpty(childrenWidgetId)) {
               for (int i = 0; i < childrenWidgetId.size(); i++) {
                  childrenWidgetId.set(i, id2Map.get(childrenWidgetId.getString(i)));
               }

               config.put(key, childrenWidgetId);
            }
         }

         String configJson = JSONObject.toJSONString(config);
         this.cutConfig(configJson, parentComp);
         targetPageCompList.stream().filter(page -> idMap.get(parentComp.getId()).equals(page.getId())).forEach(page -> this.cutConfig(configJson, page));
         targetPageCompList.addAll(childrenList);
      });
      List<LcdpModulePageCompBean> modulePageCompList = pageCompDataList.stream()
         .filter(comp -> childrenIdList.contains(comp.getParentId()))
         .collect(Collectors.toList());
      if (!modulePageCompList.isEmpty()) {
         this.updateChildrenComp(id2Map, targetPageCompList, parentId2PageCompListMap, pageCompDataList);
      }
   }

   private void perfectPageComps(List<LcdpModulePageCompBean> modulePageCompList, Map<Long, Long> resourceIdMap, List<LcdpModulePageI18nBean> insertI18nList) {
      modulePageCompList.forEach(page -> page.setModulePageHistoryId(resourceIdMap.get(page.getModulePageHistoryId())));
      insertI18nList.forEach(i18n -> i18n.setModulePageHistoryId(resourceIdMap.get(i18n.getModulePageHistoryId())));
      List<Long> historyIdList = modulePageCompList.stream().map(LcdpModulePageCompBean::getModulePageHistoryId).distinct().collect(Collectors.toList());
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService.selectListByIds(historyIdList);
      Map<Long, Long> histroryId2ResourceIdMap = resourceHistoryList.stream()
         .collect(Collectors.toMap(LcdpResourceHistoryBean::getId, LcdpResourceHistoryBean::getResourceId, (v1, v2) -> v2));
      modulePageCompList.forEach(page -> page.setModulePageId(histroryId2ResourceIdMap.get(page.getModulePageHistoryId())));
      insertI18nList.forEach(i18n -> i18n.setModulePageId(histroryId2ResourceIdMap.get(i18n.getModulePageHistoryId())));
   }

   private void updateLockResource(List<LcdpResourceBean> toLockResourceList) {
      List<String> toLockIdList = toLockResourceList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
      List<LcdpResourceLockBean> lockResourceList = this.lcdpResourceLockService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", toLockIdList).filter(MatchPattern.OR), new Order[0]);
      lockResourceList.forEach(lock -> lock.setLockUserId(LocalContextHelper.getLoginUserId()));
      this.lcdpResourceLockService.getDao().update(lockResourceList, "LOCKUSERID");
   }

   private void lockResource(List<LcdpResourceBean> resourceList) {
      if (!ObjectUtils.isEmpty(resourceList)) {
         List<LcdpResourceLockBean> lockList = resourceList.stream().map(resource -> {
            LcdpResourceLockBean lock = new LcdpResourceLockBean();
            lock.setId(ApplicationContextHelper.getNextIdentity());
            lock.setLockUserId(LocalContextHelper.getLoginUserId());
            lock.setResourceId(resource.getId().toString());
            lock.setResourceCategory(resource.getResourceCategory());
            return lock;
         }).collect(Collectors.toList());
         this.lcdpResourceLockService.getDao().insert(lockList);
      }
   }

   private boolean noReturnDataFilter(LcdpResourceBean resourceBean) {
      return LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resourceBean.getResourceCategory())
         && ObjectUtils.isEmpty(resourceBean.getEffectVersion())
         && !LocalContextHelper.getLoginUserId().equals(resourceBean.getCreatedById());
   }

   private LcdpResourceDTO buildLcdpResourceDTO(LcdpResourceHistoryBean resourceHistory) {
      LcdpResourceDTO resourceDTO = LcdpResourceDTO.of(resourceHistory);
      if ("view".equals(resourceHistory.getResourceCategory())) {
         List<LcdpModulePageCompBean> modulePageCompList = this.lcdpModulePageCompService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistory.getId()).filter(MatchPattern.SEQ), new Order[0]);
         List<LcdpModulePageCompBean> dealPageCompList = modulePageCompList.stream().map(e -> {
            LcdpModulePageCompBean pageComp = new LcdpModulePageCompBean();
            BeanUtils.copyProperties(e, pageComp);
            return pageComp;
         }).collect(Collectors.toList());
         dealPageCompList.stream().filter(e -> !StringUtils.isEmpty(e.getConfigExtend())).forEach(e -> e.setConfig(e.getConfig() + e.getConfigExtend()));
         resourceDTO.setComponents(dealPageCompList);
         if (StringUtils.equals("yes", resourceHistory.getEffectFlag())) {
            resourceDTO.setI18n(this.lcdpModulePageI18nService.selectPageI18nMessage(resourceHistory.getId()));
         } else {
            resourceDTO.setI18n(this.lcdpModulePageI18nService.selectPageI18nMessage(resourceHistory.getId()));
            List<LcdpPageI18nCodeBean> pageI18nCodeList = this.lcdpPageI18nCodeService
               .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
            resourceDTO.setDependentI18n(pageI18nCodeList.stream().map(LcdpPageI18nCodeBean::getCode).collect(Collectors.toList()));
         }

         resourceDTO.setPageConfig(this.buildPageConfig(resourceHistory.getPath()));
      }

      return resourceDTO;
   }

   private void bulidFolder(List<LcdpResourceBean> resourceList, LcdpResourceBean resource) {
      LcdpResourceBean pageFloder = new LcdpResourceBean();
      pageFloder.setId(Long.valueOf(resource.getId() + "777"));
      pageFloder.setOrderNo(1L);
      pageFloder.setResourceDesc("page");
      pageFloder.setResourceName("page");
      pageFloder.setResourceCategory("folder");
      pageFloder.setParentId(resource.getId());
      resourceList.add(pageFloder);
      resourceList.stream()
         .filter(re -> "view".equals(re.getResourceCategory()) && re.getParentId().equals(resource.getId()))
         .forEach(re -> re.setParentId(pageFloder.getId()));
      LcdpResourceBean frontFloder = new LcdpResourceBean();
      frontFloder.setId(Long.valueOf(resource.getId() + "888"));
      frontFloder.setOrderNo(1L);
      frontFloder.setResourceDesc("client");
      frontFloder.setResourceName("client");
      frontFloder.setResourceCategory("folder");
      frontFloder.setParentId(resource.getId());
      resourceList.add(frontFloder);
      resourceList.stream()
         .filter(re -> "js".equals(re.getResourceCategory()) && re.getParentId().equals(resource.getId()))
         .forEach(re -> re.setParentId(frontFloder.getId()));
      LcdpResourceBean backFloder = new LcdpResourceBean();
      backFloder.setId(Long.valueOf(resource.getId() + "999"));
      backFloder.setOrderNo(1L);
      backFloder.setResourceDesc("server");
      backFloder.setResourceName("server");
      backFloder.setResourceCategory("folder");
      backFloder.setParentId(resource.getId());
      resourceList.add(backFloder);
      resourceList.stream()
         .filter(re -> "java".equals(re.getResourceCategory()) && re.getParentId().equals(resource.getId()))
         .forEach(re -> re.setParentId(backFloder.getId()));
      LcdpResourceBean mapperFloder = new LcdpResourceBean();
      mapperFloder.setId(Long.valueOf(resource.getId() + "666"));
      mapperFloder.setOrderNo(1L);
      mapperFloder.setResourceDesc("mapper");
      mapperFloder.setResourceName("mapper");
      mapperFloder.setResourceCategory("folder");
      mapperFloder.setParentId(resource.getId());
      resourceList.add(mapperFloder);
      resourceList.stream()
         .filter(re -> "mapper".equals(re.getResourceCategory()) && re.getParentId().equals(resource.getId()))
         .forEach(re -> re.setParentId(mapperFloder.getId()));
   }

   private List<LcdpResourceBean> generateFilePath(List<LcdpResourceBean> resourceList) {
      LcdpResourceBean resource = resourceList.get(0);
      if (!LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resource.getResourceCategory())) {
         return resourceList;
      } else {
         Long parentId = resource.getParentId();
         LcdpResourceBean moduleResource = this.selectById(parentId);
         LcdpResourceBean categoryResource = this.selectById(moduleResource.getParentId());
         String path = categoryResource.getResourceName() + "." + moduleResource.getResourceName() + ".";
         resourceList.forEach(r -> {
            String var2x = r.getResourceCategory();
            switch (var2x) {
               case "view":
                  r.setPath(path + "page." + r.getResourceName());
                  break;
               case "js":
                  r.setPath(path + "client." + r.getResourceName());
                  break;
               case "java":
                  r.setPath(path + "server." + r.getResourceName());
                  break;
               case "mapper":
                  r.setPath(path + "mapper." + r.getResourceName());
            }
         });
         return resourceList;
      }
   }

   private List<LcdpResourceTreeNodeDTO> bulidResourceTreeNodeDTOS(
      List<Long> resourceIdList, List<LcdpTableBean> tableList, List<LcdpViewBean> viewList, String... operationType
   ) {
      List<LcdpResourceBean> resourceList = new ArrayList<>();
      List<LcdpResourceTreeNodeDTO> resourceDTOList = new ArrayList<>();
      if (!resourceIdList.isEmpty()) {
         List<LcdpResourceBean> scriptList = this.getDao()
            .selectListByOneColumnValues(
               resourceIdList,
               "ID",
               Arrays.asList("ID", "PARENTID", "PATH", "RESOURCENAME", "EFFECTVERSION", "RESOURCEDESC", "RESOURCECATEGORY", "ORDERNO"),
               new Order[]{Order.asc("RESOURCENAME")}
            );
         List<Long> moduleIdList = scriptList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
         moduleIdList = moduleIdList.stream().distinct().collect(Collectors.toList());
         List<LcdpResourceBean> mooduleList = this.selectListByFilter(
            SearchFilter.instance().match("RESOURCECATEGORY", "module").filter(MatchPattern.EQ).match("ID", moduleIdList).filter(MatchPattern.OR),
            new Order[]{Order.asc("RESOURCENAME")}
         );
         List<Long> categoryIdList = mooduleList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
         categoryIdList = categoryIdList.stream().distinct().collect(Collectors.toList());
         List<LcdpResourceBean> categoryList = this.selectListByFilter(
            SearchFilter.instance().match("RESOURCECATEGORY", "category").filter(MatchPattern.EQ).match("ID", categoryIdList).filter(MatchPattern.OR),
            new Order[]{Order.asc("RESOURCENAME")}
         );
         resourceList.addAll(scriptList);
         resourceList.addAll(mooduleList);
         resourceList.addAll(categoryList);

         for (int i = 0; i < resourceList.size(); i++) {
            LcdpResourceBean resource = resourceList.get(i);
            if ("module".equals(resource.getResourceCategory())) {
               this.bulidFolder(resourceList, resource);
            }
         }

         if (!ObjectUtils.isEmpty(operationType)) {
            List<LcdpResourceHistoryBean> historyResourceList = this.lcdpResourceHistoryService
               .selectListByFilter(
                  SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR).match("SUBMITFLAG", "no").filter(MatchPattern.SEQ),
                  new Order[0]
               );
            List<LcdpResourceHistoryBean> currentUserCheckout = historyResourceList.stream()
               .filter(his -> StringUtils.equals(his.getCreatedById(), LocalContextHelper.getLoginUserId()) && StringUtils.equals(his.getDeleteFlag(), "0"))
               .collect(Collectors.toList());
            currentUserCheckout.stream()
               .forEach(
                  e -> {
                     LcdpResourceHistoryBean history = historyResourceList.stream()
                        .filter(f -> ObjectUtils.equals(e.getId(), f.getResourceId()))
                        .findFirst()
                        .orElse(null);
                     if (history != null) {
                        e.setResourceName(history.getResourceName());
                     }
                  }
               );
            List<LcdpResourceBean> waitDeleteFolderList = new ArrayList<>();
            List<LcdpResourceBean> folderList = resourceList.stream()
               .filter(resource -> "folder".equals(resource.getResourceCategory()))
               .collect(Collectors.toList());
            Map<Long, LcdpResourceBean> folderMap = folderList.stream().collect(Collectors.toMap(LcdpResourceBean::getId, Function.identity(), (v1, v2) -> v1));
            List<Long> folderIdList = folderList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
            List<LcdpResourceBean> childList = resourceList.stream()
               .filter(resource -> folderIdList.contains(resource.getParentId()))
               .collect(Collectors.toList());
            Map<Long, List<LcdpResourceBean>> childMap = childList.stream().collect(Collectors.groupingBy(LcdpResourceBean::getParentId));
            folderMap.forEach((k, v) -> {
               if (ObjectUtils.isEmpty(childMap.get(k))) {
                  waitDeleteFolderList.add(v);
               }
            });
            resourceList.removeAll(waitDeleteFolderList);
            Map<Long, String> resourceId2UserMap = historyResourceList.stream()
               .collect(Collectors.toMap(LcdpResourceHistoryBean::getResourceId, LcdpResourceHistoryBean::getCreatedByName, (v1, v2) -> v1));
            scriptList.forEach(script -> script.setExt$Item("checkoutuser", resourceId2UserMap.get(script.getId())));
         }

         TreeDescriptor<LcdpResourceBean> descriptor = new TreeDescriptor<>("id", "parentId", "resourceName", "orderNo");
         descriptor.setParseTreeNodeFunction(t -> LcdpResourceTreeNodeDTO.of(t));
         resourceDTOList = TreeHelper.parseTreeNode(resourceList, descriptor, LcdpResourceTreeNodeDTO.class);
      }

      if (!tableList.isEmpty()) {
         LcdpResourceTreeNodeDTO tableRoot = new LcdpResourceTreeNodeDTO();
         tableRoot.setName("table");
         tableRoot.setDesc("table");
         tableRoot.setId("TABLES");
         tableRoot.setType("category");
         resourceDTOList.add(tableRoot);
         tableList.forEach(table -> {
            LcdpResourceTreeNodeDTO treeNodeDTO = LcdpResourceTreeNodeDTO.of(table);
            treeNodeDTO.setType("table");
            treeNodeDTO.setParentId(tableRoot.getId());
            treeNodeDTO.setCheckoutUser(table.getCreatedByName());
            if (null != table.getVersion()) {
               treeNodeDTO.setResourceStatus(1L == table.getVersion() && StringUtils.equals("no", table.getSubmitFlag()) ? "new" : "valid");
            }

            tableRoot.addChild(treeNodeDTO);
         });
         TreeHelper.updateChildQty(resourceDTOList);
      }

      if (!viewList.isEmpty()) {
         LcdpResourceTreeNodeDTO viewRoot = new LcdpResourceTreeNodeDTO();
         viewRoot.setName("db-view");
         viewRoot.setDesc("db-view");
         viewRoot.setId("VIEWS");
         viewRoot.setType("category");
         resourceDTOList.add(viewRoot);
         viewList.forEach(view -> {
            LcdpResourceTreeNodeDTO treeNodeDTO = LcdpResourceTreeNodeDTO.of(view);
            treeNodeDTO.setType("db-view");
            treeNodeDTO.setParentId(viewRoot.getId());
            treeNodeDTO.setCheckoutUser(view.getCreatedByName());
            if (null != view.getVersion()) {
               treeNodeDTO.setResourceStatus(1L == view.getVersion() && StringUtils.equals("no", view.getSubmitFlag()) ? "new" : "valid");
            }

            viewRoot.addChild(treeNodeDTO);
         });
         TreeHelper.updateChildQty(resourceDTOList);
      }

      return resourceDTOList;
   }

   private void insertServerScriptMethod(LcdpResourceHistoryBean resourceHistory) {
      if (StringUtils.equals("java", resourceHistory.getResourceCategory())) {
         GikamConcurrentLocker.isolated("RESOURCE_SERVER_METHOD$" + resourceHistory.getResourceId().toString());

         try {
            Long resourceId = resourceHistory.getResourceId();
            List<LcdpServerScriptMethodBean> serverScriptMethodList = new ArrayList<>();
            Class<?> javaScript = DynamicClassLoader.getInstance().loadSourceCode(resourceHistory.getClassContent());
            CodeHintsManager.register(resourceHistory.getResourceName(), javaScript);

            for (Method method : (List<Method>)Arrays.stream(javaScript.getMethods())
               .filter(methodx -> methodx.getAnnotation(Mapping.class) != null)
               .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Method::getName))), ArrayList::new))) {
               LcdpServerScriptMethodBean serverScriptMethod = new LcdpServerScriptMethodBean();
               String methodName = method.getName();
               Mapping annotation = method.getAnnotation(Mapping.class);
               String methodPath = resourceHistory.getPath() + "." + methodName;
               serverScriptMethod.setId(ApplicationContextHelper.getNextIdentity());
               serverScriptMethod.setServerScriptId(resourceId);
               serverScriptMethod.setMethodName(methodName);
               serverScriptMethod.setMethodPath(methodPath);
               serverScriptMethod.setMethodDesc(annotation.value());
               serverScriptMethod.setMappingType(annotation.type().name());
               serverScriptMethodList.add(serverScriptMethod);
               serverScriptMethod.setMethodFlag("normal");
               serverScriptMethod.setMethodCreatedById(LocalContextHelper.getLoginUserId());
               if (StringUtils.equals(resourceHistory.getEffectFlag(), "no") && resourceHistory.getVersion() == 1L) {
                  serverScriptMethod.setMethodFlag("draft");
               }
            }

            List<LcdpServerScriptMethodBean> deleteServerScriptMethodList = this.serverScriptMethodService
               .selectListByFilter(SearchFilter.instance().match("SERVERSCRIPTID", resourceId).filter(MatchPattern.EQ), new Order[0]);
            if (!deleteServerScriptMethodList.isEmpty()) {
               List<Long> deleteIdList = deleteServerScriptMethodList.stream().map(LcdpServerScriptMethodBean::getId).collect(Collectors.toList());
               this.serverScriptMethodService.getDao().deleteByIdList(deleteIdList);
            }

            this.serverScriptMethodService.getDao().insert(serverScriptMethodList);
         } catch (Exception var15) {
            throw var15;
         } finally {
            GikamConcurrentLocker.releaseLock("GIKAM_ISOLATED_CONCURRENT", "RESOURCE_SERVER_METHOD$" + resourceHistory.getResourceId().toString());
         }
      }
   }

   private String replaceClassName(String path, String content, String resourceName, String v, String m) {
      String module = path.split("\\.")[1];
      String moduleName = module.substring(0, 1).toUpperCase() + module.substring(1);
      String newClassName = moduleName + resourceName + (StringUtils.isBlank(v) ? "" : v) + (StringUtils.isBlank(m) ? "" : m);
      String oldClassName = JavaCompilerManager.getClassName(content);
      return content.replaceFirst("public\\s+class\\s+" + oldClassName, "public class " + newClassName);
   }

   private void registerSpring(List<LcdpResourceBean> resourceList) {
      for (LcdpResourceBean resource : resourceList.stream()
         .filter(f -> "java".equals(f.getResourceCategory()) && !StringUtils.isEmpty(f.getContent()))
         .collect(Collectors.toList())) {
         String script = resource.getClassContent();
         String className = JavaCompilerManager.getClassName(script);
         Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(script);
         String beanName = className.toLowerCase();
         log.info("============>spring加载类" + className);

         try {
            SpringUtils.registerBean(beanName, clazz);
            this.stringRedisTemplate.convertAndSend(LcdpMapperUtils.LCDP_SYNC_SCRIPT_TOPIC, script);
         } catch (Exception var11) {
            throw new CheckedException(
               I18nHelper.getMessage("LCDP.MODULE.RESOURCES.TIP.BACKEND_SCRIPT_REGISTRATION_SPRING_ERROR") + resource.getPath() + var11.getMessage(), var11
            );
         }

         CodeHintsManager.register(resource.getResourceName(), SpringUtils.getBean(beanName).getClass());
         LcdpResourceCache.put(resource.getPath(), resource);
         if (LcdpBaseService.class.isAssignableFrom(SpringUtils.getBean(beanName).getClass())) {
            LcdpBaseService scriptService = SpringUtils.getBean(beanName);
            String tableName = scriptService.getTable();
            RedisHelper.put("TABLE_SCRIPT_MAPPING_CACHE", tableName, beanName);
            RedisHelper.put("SCRIPT_PATH_TABLE_MAPPING_CACHE", resource.getPath(), tableName);
            RedisHelper.put("SCRIPT_TABLE_PATH_MAPPING_CACHE", tableName, resource.getPath());
         }
      }
   }

   private Map<String, Object> analysisFileContent(
      List<String> resourceIdStrList, List<String> tableNameList, List<String> viewNameList, Map<String, String> fileMap
   ) {
      List<LcdpResourceBean> resourceList = new ArrayList<>();
      List<LcdpResourceBean> scriptList = new ArrayList<>();
      List<LcdpResourceBean> moduleList = new ArrayList<>();
      List<LcdpResourceBean> categoryList = new ArrayList<>();
      List<CoreCodeCategoryBean> coreCodeCategoryList = new ArrayList<>();
      List<CoreCodeBean> codeList = new ArrayList<>();
      List<CoreMenuBean> coreMenuList = new ArrayList<>();
      List<CoreI18nMessageBean> coreI18nMessageList = new ArrayList<>();
      List<CoreAdminSelectConfigBean> coreAdminSelectConfigList = new ArrayList<>();
      List<LcdpModulePageCompBean> modulePageCompList = new ArrayList<>();
      List<LcdpModulePageI18nBean> modulePageI18nList = new ArrayList<>();
      List<LcdpPageI18nCodeBean> modulePageDependentI18nList = new ArrayList<>();

      for (String reourceId : resourceIdStrList) {
         String resourceJson = fileMap.get(reourceId);
         LcdpResourceBean resource = JsonUtils.parse(resourceJson, LcdpResourceBean.class);
         scriptList.add(resource);
      }

      scriptList = scriptList.stream()
         .filter(resource -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resource.getResourceCategory()))
         .collect(Collectors.toList());

      for (LcdpResourceBean scrip : scriptList) {
         String resourceJson = fileMap.get(scrip.getParentId().toString());
         LcdpResourceBean resource = JsonUtils.parse(resourceJson, LcdpResourceBean.class);
         List<Long> moduleIdList = moduleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         if (!moduleIdList.contains(resource.getId())) {
            moduleList.add(resource);
         }
      }

      for (LcdpResourceBean module : moduleList) {
         String resourceJson = fileMap.get(module.getParentId().toString());
         LcdpResourceBean resource = JsonUtils.parse(resourceJson, LcdpResourceBean.class);
         List<Long> categoryIdList = categoryList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         if (!categoryIdList.contains(resource.getId())) {
            categoryList.add(resource);
         }
      }

      resourceList.addAll(scriptList);
      resourceList.addAll(moduleList);
      resourceList.addAll(categoryList);
      List<LcdpTableDTO> tableDTOList = new ArrayList<>();

      for (String tableName : tableNameList) {
         String tableJson = fileMap.get(tableName);
         LcdpTableDTO tableDTO = JsonUtils.parse(tableJson, LcdpTableDTO.class);
         tableDTOList.add(tableDTO);
      }

      List<LcdpViewBean> viewList = new ArrayList<>();

      for (String viewName : viewNameList) {
         String viewJson = fileMap.get(viewName);
         LcdpViewBean view = JsonUtils.parse(viewJson, LcdpViewBean.class);
         viewList.add(view);
      }

      scriptList.stream().filter(resource -> "view".equals(resource.getResourceCategory())).forEach(sc -> {
         String viewId = sc.getId() + "view";
         String i18nId = sc.getId() + "i18n";
         String sysI18nDependenceId = sc.getId() + "sysI18nDependence";
         String modulePageCompJson = fileMap.get(viewId);
         if (!StringUtils.isEmpty(modulePageCompJson)) {
            List<LcdpModulePageCompBean> modulePageCompBeans = JSON.parseArray(modulePageCompJson).toJavaList(LcdpModulePageCompBean.class);
            modulePageCompList.addAll(modulePageCompBeans);
         }

         String modulePageI18nJson = fileMap.get(i18nId);
         if (!StringUtils.isEmpty(modulePageI18nJson)) {
            modulePageI18nList.addAll(JSON.parseArray(modulePageI18nJson).toJavaList(LcdpModulePageI18nBean.class));
         }

         String modulePageI18nDependenceJson = fileMap.get(sysI18nDependenceId);
         if (!StringUtils.isEmpty(modulePageI18nDependenceJson)) {
            modulePageDependentI18nList.addAll(JSON.parseArray(modulePageI18nDependenceJson).toJavaList(LcdpPageI18nCodeBean.class));
         }
      });
      if (!ObjectUtils.isEmpty(fileMap.get("codeCategory"))) {
         List<CoreCodeCategoryBean> codeCategorys = JSON.parseArray(fileMap.get("codeCategory")).toJavaList(CoreCodeCategoryBean.class);
         coreCodeCategoryList.addAll(codeCategorys);
      }

      if (!ObjectUtils.isEmpty(fileMap.get("code"))) {
         List<CoreCodeBean> codes = JSON.parseArray(fileMap.get("code")).toJavaList(CoreCodeBean.class);
         codeList.addAll(codes);
      }

      if (!ObjectUtils.isEmpty(fileMap.get("i18n"))) {
         List<CoreI18nMessageBean> i18n = JSON.parseArray(fileMap.get("i18n")).toJavaList(CoreI18nMessageBean.class);
         coreI18nMessageList.addAll(i18n);
      }

      if (!ObjectUtils.isEmpty(fileMap.get("adminSelectConfig"))) {
         List<CoreAdminSelectConfigBean> adminSelectConfigList = JSON.parseArray(fileMap.get("adminSelectConfig")).toJavaList(CoreAdminSelectConfigBean.class);
         coreAdminSelectConfigList.addAll(adminSelectConfigList);
      }

      if (!ObjectUtils.isEmpty(fileMap.get("menu"))) {
         List<CoreMenuBean> menuList = JSON.parseArray(fileMap.get("menu")).toJavaList(CoreMenuBean.class);
         coreMenuList.addAll(menuList);
      }

      Map<String, Object> importDataMap = new HashMap<>();
      importDataMap.put("resource", resourceList);
      importDataMap.put("pageComps", modulePageCompList);
      importDataMap.put("pageI18n", modulePageI18nList);
      importDataMap.put("pageDependentI18n", modulePageDependentI18nList);
      importDataMap.put("table", tableDTOList);
      importDataMap.put("db-view", viewList);
      importDataMap.put("codeCategory", coreCodeCategoryList);
      importDataMap.put("code", codeList);
      importDataMap.put("i18n", coreI18nMessageList);
      importDataMap.put("menu", coreMenuList);
      importDataMap.put("adminSelectConfig", coreAdminSelectConfigList);
      return importDataMap;
   }

   private List<LcdpResourceTreeNodeDTO> bulidAnalysisTreeNodeDTOS(
      Map<String, LcdpAnalysisResultDTO> tableResult,
      Map<String, LcdpAnalysisResultDTO> viewResult,
      List<LcdpResourceBean> resourceList,
      List<LcdpTableDTO> tableDTOList,
      List<LcdpViewBean> viewList
   ) {
      List<LcdpResourceTreeNodeDTO> resourceDTOList = new ArrayList<>();
      if (!resourceList.isEmpty()) {
         TreeDescriptor<LcdpResourceBean> descriptor = new TreeDescriptor<>("id", "parentId", "resourceName", "orderNo");
         descriptor.setParseTreeNodeFunction(t -> {
            LcdpResourceTreeNodeDTO resourceDTO = LcdpResourceTreeNodeDTO.of(t);
            resourceDTO.setAllowImport(true);
            return resourceDTO;
         });
         resourceDTOList = TreeHelper.parseTreeNode(resourceList, descriptor, LcdpResourceTreeNodeDTO.class);
      }

      Boolean allowImport = true;
      if (!tableResult.isEmpty()) {
         for (String tableName : tableResult.keySet()) {
            allowImport = tableResult.get(tableName).getEnable();
            if (!allowImport) {
               break;
            }
         }
      }

      if (!tableDTOList.isEmpty()) {
         LcdpResourceTreeNodeDTO tableRoot = new LcdpResourceTreeNodeDTO();
         tableRoot.setName("table");
         tableRoot.setDesc("table");
         tableRoot.setId(ApplicationContextHelper.getNextIdentity().toString());
         tableRoot.setType("category");
         tableRoot.setAllowImport(allowImport);
         resourceDTOList.add(tableRoot);

         for (LcdpTableDTO table : tableDTOList) {
            LcdpResourceTreeNodeDTO treeNodeDTO = new LcdpResourceTreeNodeDTO();
            treeNodeDTO.setId(table.getTableName());
            treeNodeDTO.setName(table.getTableName());
            treeNodeDTO.setDesc(table.getTableDesc());
            treeNodeDTO.setOrderNo(1L);
            treeNodeDTO.setType("table");
            treeNodeDTO.setParentId(tableRoot.getId());
            treeNodeDTO.setAllowImport(tableResult.get(table.getTableName()).getEnable());
            treeNodeDTO.setAnalysisResult(tableResult.get(table.getTableName()).getAnalysisResultList().toString());
            tableRoot.addChild(treeNodeDTO);
         }

         TreeHelper.updateChildQty(resourceDTOList);
      }

      if (!viewList.isEmpty()) {
         LcdpResourceTreeNodeDTO viewRoot = new LcdpResourceTreeNodeDTO();
         viewRoot.setName("db-view");
         viewRoot.setDesc("db-view");
         viewRoot.setId(ApplicationContextHelper.getNextIdentity().toString());
         viewRoot.setType("category");
         resourceDTOList.add(viewRoot);
         viewRoot.setAllowImport(true);

         for (LcdpViewBean view : viewList) {
            LcdpResourceTreeNodeDTO treeNodeDTO = new LcdpResourceTreeNodeDTO();
            treeNodeDTO.setId(view.getViewName());
            treeNodeDTO.setName(view.getViewName());
            treeNodeDTO.setDesc(view.getViewName());
            treeNodeDTO.setOrderNo(1L);
            treeNodeDTO.setType("db-view");
            treeNodeDTO.setParentId(viewRoot.getId());
            treeNodeDTO.setAllowImport(viewResult.get(view.getViewName()).getEnable());
            treeNodeDTO.setAnalysisResult(viewResult.get(view.getViewName()).getAnalysisResultList().toString());
            viewRoot.addChild(treeNodeDTO);
            if (viewRoot.getAllowImport() && !treeNodeDTO.getAllowImport()) {
               viewRoot.setAllowImport(false);
            }
         }

         TreeHelper.updateChildQty(resourceDTOList);
      }

      return resourceDTOList;
   }

   private List<LcdpTableBean> importTableData(
      List<String> tableNameList, Map<String, String> fileMap, LcdpSubmitLogBean submitLog, LcdpSubmitLogBean autoSubmitLog
   ) {
      List<LcdpTableDTO> currentLcdpTableDTOS = this.lcdpTableService.selectPhysicalTableInfoList(tableNameList);
      List<LcdpTableBean> tableList = this.lcdpTableService
         .selectListByFilter(SearchFilter.instance().match("TABLENAME", tableNameList).filter(MatchPattern.OCISEQ), new Order[0]);
      Map<String, Optional<LcdpTableBean>> table2VersionMap = tableList.stream()
         .collect(Collectors.groupingBy(LcdpTableBean::getTableName, Collectors.maxBy(Comparator.comparing(LcdpTableBean::getVersion))));
      List<LcdpTableBean> unCommitTableList = tableList.stream().filter(table -> "no".equals(table.getSubmitFlag())).collect(Collectors.toList());
      List<String> unCommitTableNameList = unCommitTableList.stream().map(LcdpTableBean::getTableName).collect(Collectors.toList());
      this.lcdpResourceLockService.unLock(unCommitTableNameList);
      List<LcdpResourceVersionBean> insertVersionList = unCommitTableList.stream().map(table -> {
         LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
         resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
         resourceVersion.setResourceId(table.getTableName());
         resourceVersion.setResourceName(table.getTableName());
         resourceVersion.setLogId(autoSubmitLog.getId());
         resourceVersion.setResourceCategory(LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get("table"));
         resourceVersion.setEffectFlag("no");
         resourceVersion.setVersion(table.getVersion());
         resourceVersion.setCommit(autoSubmitLog.getCommit());
         resourceVersion.setEditTime(table.getCreatedTime());
         resourceVersion.setResourcePath(table.getTableName());
         EntityHelper.assignCreatedElement(resourceVersion);
         return resourceVersion;
      }).collect(Collectors.toList());
      this.lcdpResourceVersionService.getDao().insert(insertVersionList);
      List<LcdpTableBean> insertTableList = new ArrayList<>();
      List<LcdpTableFieldBean> insertFieldList = new ArrayList<>();
      List<LcdpTableIndexBean> insertIndexList = new ArrayList<>();

      for (String tableName : tableNameList) {
         LcdpTableDTO importTable = JsonUtils.parse(fileMap.get(tableName), LcdpTableDTO.class);
         boolean isExist = currentLcdpTableDTOS.stream().anyMatch(table -> StringUtils.equals(table.getTableName(), tableName));
         if (isExist) {
            this.modifyTableData(currentLcdpTableDTOS, table2VersionMap, insertTableList, insertFieldList, insertIndexList, tableName, importTable);
         } else {
            this.addImportTableData(insertTableList, insertFieldList, insertIndexList, importTable);
         }
      }

      this.lcdpTableService.getDao().insert(insertTableList);
      this.lcdpResourceVersionService.insertByTableList(insertTableList, submitLog);
      this.lcdpTableIndexService.getDao().insert(insertIndexList);
      this.lcdpTableFieldService.getDao().insert(insertFieldList);
      LcdpSubmitLogBean querySubmitLog = this.submitLogService.selectByIdIfPresent(autoSubmitLog.getId());
      if (!unCommitTableList.isEmpty() && querySubmitLog == null) {
         this.submitLogService.getDao().insert(autoSubmitLog);
      }

      return insertTableList;
   }

   private List<LcdpViewBean> importViewData(
      List<String> viewNameList, Map<String, String> fileMap, LcdpSubmitLogBean submitLog, LcdpSubmitLogBean autoSubmitLog
   ) {
      List<LcdpViewBean> physicalViewInfoList = this.lcdpDatabaseService.selectPhysicalViewInfoList(viewNameList);
      List<LcdpViewBean> viewVersionList = this.lcdpViewService
         .selectListByFilter(SearchFilter.instance().match("VIEWNAME", viewNameList).filter(MatchPattern.OCISEQ), new Order[0]);
      Map<String, Optional<LcdpViewBean>> view2VersionMap = viewVersionList.stream()
         .collect(Collectors.groupingBy(LcdpViewBean::getViewName, Collectors.maxBy(Comparator.comparing(LcdpViewBean::getVersion))));
      List<LcdpViewBean> unCommitViewList = viewVersionList.stream().filter(view -> "no".equals(view.getSubmitFlag())).collect(Collectors.toList());
      List<String> unCommitViewNameList = unCommitViewList.stream().map(LcdpViewBean::getTableName).collect(Collectors.toList());
      this.lcdpResourceLockService.unLock(unCommitViewNameList);
      List<LcdpResourceVersionBean> insertVersionList = unCommitViewList.stream().map(view -> {
         LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
         resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
         resourceVersion.setResourceId(view.getViewName());
         resourceVersion.setResourceName(view.getViewName());
         resourceVersion.setLogId(autoSubmitLog.getId());
         resourceVersion.setResourceCategory(LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get("table"));
         resourceVersion.setEffectFlag("no");
         view.setSubmitFlag("yes");
         resourceVersion.setVersion(view.getVersion());
         resourceVersion.setCommit(autoSubmitLog.getCommit());
         resourceVersion.setEditTime(view.getCreatedTime());
         resourceVersion.setResourcePath(view.getViewName());
         EntityHelper.assignCreatedElement(resourceVersion);
         return resourceVersion;
      }).collect(Collectors.toList());
      this.lcdpResourceVersionService.getDao().insert(insertVersionList);
      this.lcdpViewService.updateIfChanged(unCommitViewList);
      List<LcdpViewBean> viewList = new ArrayList<>();
      List<LcdpResourceVersionBean> versionList = new ArrayList<>();
      viewNameList.forEach(
         viewName -> {
            LcdpViewBean matchPhysicalView = physicalViewInfoList.stream()
               .filter(physicalView -> StringUtils.equalsIgnoreCase(viewName, physicalView.getViewName()))
               .findFirst()
               .orElse(null);
            LcdpViewBean matchSubmitView = JsonUtils.parse(fileMap.get(viewName), LcdpViewBean.class);
            if (!ObjectUtils.isEmpty(matchSubmitView)) {
               RedisHelper.evict("TC", viewName);
               RedisHelper.evict("EC", viewName);
               if (ObjectUtils.isEmpty(matchPhysicalView)) {
                  this.lcdpDatabaseService.createPhysicalView(matchSubmitView);
               } else {
                  this.lcdpDatabaseService.alterPhysicalView(matchSubmitView);
               }

               matchSubmitView.setId(ApplicationContextHelper.getNextIdentity());
               matchSubmitView.setSubmitFlag("yes");
               matchSubmitView.setVersion(ObjectUtils.isEmpty(view2VersionMap.get(viewName)) ? 2L : view2VersionMap.get(viewName).get().getVersion() + 1L);
               viewList.add(matchSubmitView);
               LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
               resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
               resourceVersion.setLogId(submitLog.getId());
               resourceVersion.setResourceId(matchSubmitView.getViewName());
               resourceVersion.setResourceName(matchSubmitView.getViewName());
               resourceVersion.setResourcePath(matchSubmitView.getViewName());
               resourceVersion.setResourceCategory(LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get("db-view"));
               resourceVersion.setEffectFlag("yes");
               resourceVersion.setVersion(matchSubmitView.getVersion());
               resourceVersion.setCommit(submitLog.getCommit());
               EntityHelper.assignCreatedElement(resourceVersion);
               resourceVersion.setEditTime(matchSubmitView.getCreatedTime());
               versionList.add(resourceVersion);
            }
         }
      );
      this.lcdpResourceVersionService.getDao().insert(versionList);
      this.lcdpViewService.getDao().insert(viewList);
      LcdpSubmitLogBean querySubmitLog = this.submitLogService.selectByIdIfPresent(autoSubmitLog.getId());
      if (!unCommitViewNameList.isEmpty() && querySubmitLog == null) {
         this.submitLogService.getDao().insert(autoSubmitLog);
      }

      return viewList;
   }

   private void addImportTableData(
      List<LcdpTableBean> insertTableList, List<LcdpTableFieldBean> insertFieldList, List<LcdpTableIndexBean> insertIndexList, LcdpTableDTO importTable
   ) {
      LcdpTableBean table = new LcdpTableBean();
      table.setId(ApplicationContextHelper.getNextIdentity());
      table.setTableName(importTable.getTableName());
      table.setTableDesc(importTable.getTableDesc());
      table.setMasterTableName(importTable.getMasterTableName());
      table.setReferColumn(importTable.getReferColumn());
      List<LcdpTableFieldBean> fields = importTable.getFieldList().stream().map(dto -> {
         LcdpTableFieldBean field = new LcdpTableFieldBean();
         BeanUtils.copyProperties(dto, field);
         field.setId(ApplicationContextHelper.getNextIdentity());
         field.setTableId(table.getId());
         field.setFieldOperationType("add");
         EntityHelper.assignCreatedElement(field);
         return field;
      }).collect(Collectors.toList());
      insertFieldList.addAll(fields);
      List<LcdpTableIndexBean> indexes = importTable.getIndexList().stream().map(dto -> {
         LcdpTableIndexBean index = new LcdpTableIndexBean();
         BeanUtils.copyProperties(dto, index);
         index.setId(ApplicationContextHelper.getNextIdentity());
         index.setTableId(table.getId());
         index.setIndexOperationType("add");
         EntityHelper.assignCreatedElement(index);
         return index;
      }).collect(Collectors.toList());
      insertIndexList.addAll(indexes);
      String sql = this.lcdpTableService.createPhysicalTable(table, fields, indexes);
      table.setSql(sql);
      table.setVersion(1L);
      table.setSubmitFlag("yes");
      EntityHelper.assignCreatedElement(table);
      insertTableList.add(table);
   }

   private void modifyTableData(
      List<LcdpTableDTO> currentLcdpTableDTOS,
      Map<String, Optional<LcdpTableBean>> table2VersionMap,
      List<LcdpTableBean> insertTableList,
      List<LcdpTableFieldBean> insertFieldList,
      List<LcdpTableIndexBean> insertIndexList,
      String tableName,
      LcdpTableDTO importTable
   ) {
      LcdpTableDTO currentTable = currentLcdpTableDTOS.stream()
         .filter(currentLcdpTableDTO -> StringUtils.equals(tableName, currentLcdpTableDTO.getTableName()))
         .findAny()
         .get();
      List<LcdpTableFieldBean> analyzedFieldList = LcdpTableUtils.analyzeFieldOps(importTable.getFieldList(), currentTable.getFieldList());
      List<LcdpTableIndexBean> analyzedIndexList = LcdpTableUtils.analyzeIndexOps(importTable.getIndexList(), currentTable.getIndexList());
      LcdpTableBean table = new LcdpTableBean();
      table.setId(ApplicationContextHelper.getNextIdentity());
      table.setTableName(currentTable.getTableName());
      table.setTableDesc(importTable.getTableDesc());
      table.setMasterTableName(importTable.getMasterTableName());
      table.setReferColumn(importTable.getReferColumn());
      analyzedFieldList.forEach(field -> {
         field.setId(ApplicationContextHelper.getNextIdentity());
         field.setTableId(table.getId());
      });
      insertFieldList.addAll(analyzedFieldList);
      analyzedIndexList.forEach(index -> {
         index.setId(ApplicationContextHelper.getNextIdentity());
         index.setTableId(table.getId());
      });
      insertIndexList.addAll(analyzedIndexList);
      RedisHelper.evict("TC", table.getTableName());
      RedisHelper.evict("EC", table.getTableName());
      this.lcdpTableService.alterPhysicalTable(currentTable, table, analyzedFieldList, analyzedIndexList);
      String sql = this.lcdpTableService.generateCreateSql(importTable);
      table.setSql(sql);
      LcdpTableBean exitTable = null == table2VersionMap.get(table.getTableName()) ? null : table2VersionMap.get(table.getTableName()).get();
      table.setVersion(ObjectUtils.isEmpty(exitTable) ? 2L : exitTable.getVersion() + 1L);
      table.setSubmitFlag("yes");
      EntityHelper.assignCreatedElement(table);
      insertTableList.add(table);
   }

   private List<LcdpResourceBean> importResourceData(
      List<LcdpResourceBean> resourceList,
      Map<Long, List<LcdpModulePageCompBean>> resourceId2PageCompMap,
      Map<Long, List<LcdpModulePageI18nBean>> resourceId2PageI18nMap,
      Map<Long, List<LcdpPageI18nCodeBean>> resourceId2PageDependentI18nMap,
      LcdpSubmitLogBean submitLog,
      LcdpSubmitLogBean autoSubmitLog,
      StringBuilder importJavaRecord
   ) {
      List<LcdpResourceBean> importDataList = new ArrayList<>();
      List<LcdpResourceBean> scriptList = resourceList.stream()
         .filter(resourcex -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resourcex.getResourceCategory()))
         .collect(Collectors.toList());
      List<String> pathList = scriptList.stream().map(LcdpResourceBean::getPath).collect(Collectors.toList());
      List<LcdpResourceBean> currentResourceList = this.getDao()
         .selectListByOneColumnValues(
            pathList,
            "PATH",
            Arrays.asList("ID", "PARENTID", "PATH", "RESOURCENAME", "DELETEFLAG", "EFFECTVERSION", "RESOURCEDESC", "RESOURCECATEGORY"),
            new Order[0]
         );
      currentResourceList = currentResourceList.stream().filter(resourcex -> StringUtils.equals(resourcex.getDeleteFlag(), "0")).collect(Collectors.toList());
      Map<String, LcdpResourceBean> path2ResourceMap = currentResourceList.stream()
         .collect(Collectors.toMap(LcdpResourceBean::getPath, Function.identity(), (v1, v2) -> v2));
      List<LcdpResourceBean> overwriteList = new ArrayList<>();
      List<LcdpResourceBean> needAddList = new ArrayList<>();

      for (LcdpResourceBean resource : scriptList) {
         if (path2ResourceMap.get(resource.getPath()) != null) {
            overwriteList.add(resource);
         } else {
            needAddList.add(resource);
         }
      }

      if (!overwriteList.isEmpty()) {
         this.submitLogService.getDao().insert(autoSubmitLog);
         List<LcdpResourceBean> overwritedResourceList = this.dealOverwriteResourceData(
            resourceId2PageCompMap,
            resourceId2PageI18nMap,
            resourceId2PageDependentI18nMap,
            currentResourceList,
            overwriteList,
            submitLog,
            autoSubmitLog,
            importJavaRecord
         );
         importDataList.addAll(
            overwritedResourceList.stream()
               .filter(resourcex -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resourcex.getResourceCategory()))
               .collect(Collectors.toList())
         );

         for (LcdpResourceBean resourcex : overwritedResourceList.stream()
            .filter(resourcexx -> "mapper".equals(resourcexx.getResourceCategory()))
            .collect(Collectors.toList())) {
            if ("mapper".equals(resourcex.getResourceCategory())) {
               LcdpMapperUtils.loadMapper(resourcex.getPath(), true, resourcex.getContent());
               LcdpMapperUtils.syncMapper(resourcex.getPath(), true, resourcex.getContent());
               LcdpMapperUtils.unloadMapper(resourcex.getPath(), false);
            }
         }
      }

      if (!needAddList.isEmpty()) {
         List<LcdpResourceBean> addResourceList = this.dealNewAddResourceData(
            resourceList, resourceId2PageCompMap, resourceId2PageI18nMap, resourceId2PageDependentI18nMap, needAddList, submitLog, importJavaRecord
         );
         importDataList.addAll(
            addResourceList.stream()
               .filter(resourcexx -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resourcexx.getResourceCategory()))
               .collect(Collectors.toList())
         );

         for (LcdpResourceBean resourcexx : addResourceList.stream()
            .filter(resourcexxx -> "mapper".equals(resourcexxx.getResourceCategory()))
            .collect(Collectors.toList())) {
            LcdpMapperUtils.loadMapper(resourcexx.getPath(), true, resourcexx.getContent());
            LcdpMapperUtils.syncMapper(resourcexx.getPath(), true, resourcexx.getContent());
         }
      }

      return importDataList;
   }

   private List<LcdpResourceBean> dealNewAddResourceData(
      List<LcdpResourceBean> resourceList,
      Map<Long, List<LcdpModulePageCompBean>> resourceId2PageCompMap,
      Map<Long, List<LcdpModulePageI18nBean>> resourceId2PageI18nMap,
      Map<Long, List<LcdpPageI18nCodeBean>> resourceId2PageDependentI18nMap,
      List<LcdpResourceBean> needAddList,
      LcdpSubmitLogBean submitLog,
      StringBuilder importJavaRecord
   ) {
      List<LcdpResourceBean> actualImportResourceList = needAddList.stream().collect(Collectors.toList());
      List<LcdpResourceBean> insertResourceList = new ArrayList<>();
      List<LcdpResourceHistoryBean> insertResourceHistoryList = new ArrayList<>();
      List<LcdpModulePageCompBean> insertMoudlePageCompList = new ArrayList<>();
      List<LcdpModulePageI18nBean> insertPageI18nList = new ArrayList<>();
      List<LcdpPageI18nCodeBean> insertPageDependentI18nList = new ArrayList<>();
      List<String> categoryNameList = actualImportResourceList.stream().map(resource -> resource.getPath().split("\\.")[0]).collect(Collectors.toList());
      List<String> prepareAddCategoryNameList = new ArrayList<>();
      prepareAddCategoryNameList.addAll(categoryNameList);
      List<LcdpResourceBean> categoryList = this.selectListByFilter(
         SearchFilter.instance()
            .match("RESOURCENAME", categoryNameList)
            .filter(MatchPattern.OR)
            .match("RESOURCECATEGORY", "category")
            .filter(MatchPattern.SEQ)
            .match("DELETEFLAG", "0")
            .filter(MatchPattern.EQ),
         new Order[0]
      );
      List<String> currentCategoryNameList = categoryList.stream().map(LcdpResourceBean::getResourceName).collect(Collectors.toList());
      prepareAddCategoryNameList.removeAll(currentCategoryNameList);
      if (!prepareAddCategoryNameList.isEmpty()) {
         Map<Long, Long> categoryIdMap = new HashMap<>();
         Map<Long, Long> moduleIdMap = new HashMap<>();
         List<LcdpResourceBean> prepareCategoryList = resourceList.stream()
            .filter(resource -> prepareAddCategoryNameList.contains(resource.getResourceName()))
            .collect(Collectors.toList());
         List<Long> categoryIdList = prepareCategoryList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         prepareCategoryList.forEach(category -> {
            Long id = ApplicationContextHelper.getNextIdentity();
            categoryIdMap.put(category.getId(), id);
            category.setId(id);
            EntityHelper.assignCreatedElement(category);
         });
         insertResourceList.addAll(prepareCategoryList);
         List<LcdpResourceBean> prepareModuleList = resourceList.stream()
            .filter(resource -> categoryIdList.contains(resource.getParentId()))
            .collect(Collectors.toList());
         List<Long> moduleIdList = prepareModuleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         prepareModuleList.forEach(module -> {
            Long id = ApplicationContextHelper.getNextIdentity();
            moduleIdMap.put(module.getId(), id);
            module.setId(id);
            module.setParentId(categoryIdMap.get(module.getParentId()));
            EntityHelper.assignCreatedElement(module);
         });
         insertResourceList.addAll(prepareModuleList);
         List<LcdpResourceBean> prepareScriptList = actualImportResourceList.stream()
            .filter(resource -> moduleIdList.contains(resource.getParentId()))
            .collect(Collectors.toList());
         prepareScriptList.forEach(
            script -> {
               Long resourceId = script.getId();
               Long id = ApplicationContextHelper.getNextIdentity();
               script.setId(id);
               script.setParentId(moduleIdMap.get(script.getParentId()));
               script.setEffectVersion(1L);
               EntityHelper.assignCreatedElement(script);
               this.dealHistoryAndPageCompData(
                  script,
                  insertResourceHistoryList,
                  resourceId2PageCompMap,
                  resourceId2PageI18nMap,
                  resourceId2PageDependentI18nMap,
                  resourceId,
                  insertMoudlePageCompList,
                  insertPageI18nList,
                  insertPageDependentI18nList
               );
            }
         );
         insertResourceList.addAll(prepareScriptList);
      }

      Map<String, Long> categoryNameIdMap = categoryList.stream().collect(Collectors.toMap(LcdpResourceBean::getResourceName, LcdpResourceBean::getId));
      List<LcdpResourceBean> tempResourceList = actualImportResourceList.stream()
         .filter(resource -> currentCategoryNameList.contains(resource.getPath().split("\\.")[0]))
         .collect(Collectors.toList());
      Map<String, String> module2CategoryMap = new HashMap<>();

      for (LcdpResourceBean resource : tempResourceList) {
         String[] pathArray = resource.getPath().split("\\.");
         String categoryName = pathArray[0];
         String moduleName = pathArray[1];
         module2CategoryMap.put(moduleName, categoryName);
      }

      Set<String> moduleNameSet = module2CategoryMap.keySet();
      List<String> moduleNameList = moduleNameSet.stream().collect(Collectors.toList());
      List<String> prepareAddModuleNameList = new ArrayList<>();
      prepareAddModuleNameList.addAll(moduleNameList);
      List<LcdpResourceBean> moduleList = this.selectListByFilter(
         SearchFilter.instance()
            .match("RESOURCENAME", moduleNameList)
            .filter(MatchPattern.OR)
            .match("RESOURCECATEGORY", "module")
            .filter(MatchPattern.SEQ)
            .match("DELETEFLAG", "0")
            .filter(MatchPattern.EQ),
         new Order[0]
      );
      List<String> currentmoduleNameList = moduleList.stream().map(LcdpResourceBean::getResourceName).collect(Collectors.toList());
      prepareAddModuleNameList.removeAll(currentmoduleNameList);
      if (!prepareAddModuleNameList.isEmpty()) {
         Map<Long, Long> ModuleIdMap = new HashMap<>();
         List<LcdpResourceBean> prepareModuleList = resourceList.stream()
            .filter(resource -> prepareAddModuleNameList.contains(resource.getResourceName()))
            .collect(Collectors.toList());
         List<Long> moduleIdList = prepareModuleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         prepareModuleList.forEach(module -> {
            Long id = ApplicationContextHelper.getNextIdentity();
            ModuleIdMap.put(module.getId(), id);
            module.setId(id);
            module.setParentId(categoryNameIdMap.get(module2CategoryMap.get(module.getResourceName())));
            EntityHelper.assignCreatedElement(module);
         });
         List<LcdpResourceBean> prepareScriptList = actualImportResourceList.stream()
            .filter(resource -> moduleIdList.contains(resource.getParentId()))
            .collect(Collectors.toList());
         insertResourceList.addAll(prepareModuleList);
         prepareScriptList.forEach(
            script -> {
               Long resourceId = script.getId();
               Long id = ApplicationContextHelper.getNextIdentity();
               script.setId(id);
               script.setParentId(ModuleIdMap.get(script.getParentId()));
               script.setEffectVersion(1L);
               EntityHelper.assignCreatedElement(script);
               this.dealHistoryAndPageCompData(
                  script,
                  insertResourceHistoryList,
                  resourceId2PageCompMap,
                  resourceId2PageI18nMap,
                  resourceId2PageDependentI18nMap,
                  resourceId,
                  insertMoudlePageCompList,
                  insertPageI18nList,
                  insertPageDependentI18nList
               );
            }
         );
         insertResourceList.addAll(prepareScriptList);
      }

      actualImportResourceList.removeIf(resource -> prepareAddCategoryNameList.contains(resource.getPath().split("\\.")[0]));
      actualImportResourceList.removeIf(resource -> prepareAddModuleNameList.contains(resource.getPath().split("\\.")[1]));
      if (!actualImportResourceList.isEmpty()) {
         Map<String, Long> moduleName2IdMap = moduleList.stream().collect(Collectors.toMap(LcdpResourceBean::getResourceName, LcdpResourceBean::getId));
         actualImportResourceList.forEach(
            script -> {
               Long resourceId = script.getId();
               script.setId(ApplicationContextHelper.getNextIdentity());
               script.setEffectVersion(1L);
               script.setParentId(moduleName2IdMap.get(script.getPath().split("\\.")[1]));
               this.dealHistoryAndPageCompData(
                  script,
                  insertResourceHistoryList,
                  resourceId2PageCompMap,
                  resourceId2PageI18nMap,
                  resourceId2PageDependentI18nMap,
                  resourceId,
                  insertMoudlePageCompList,
                  insertPageI18nList,
                  insertPageDependentI18nList
               );
            }
         );
         insertResourceList.addAll(actualImportResourceList);
      }

      insertResourceList.stream().filter(resource -> "java".equals(resource.getResourceCategory())).forEach(resource -> {
         String content = resource.getClassContent();
         if (!StringUtils.isEmpty(content)) {
            String classContent = this.replaceClassName(resource.getPath(), content, resource.getResourceName(), "v" + resource.getEffectVersion(), null);
            resource.setClassContent(classContent);
            resource.setClassName(JavaCompilerManager.getClassName(classContent));
         }
      });
      this.getDao().insert(insertResourceList);
      this.lcdpResourceHistoryService.getDao().insert(insertResourceHistoryList);
      this.lcdpModulePageCompService.getDao().insert(insertMoudlePageCompList);
      this.lcdpModulePageI18nService.getDao().insert(insertPageI18nList);
      this.lcdpPageI18nCodeService.getDao().insert(insertPageDependentI18nList);
      this.lcdpResourceVersionService.insertByHistoryResourceList(insertResourceHistoryList, submitLog);
      List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
      insertResourceHistoryList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
      insertResourceList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
      this.addCompDocument(insertResourceList, insertResourceHistoryList, documentList);
      LuceneIndexManager.addIndex(documentList);
      Map<Long, String> resourceId2CategoryMap = insertResourceHistoryList.stream()
         .collect(Collectors.toMap(LcdpResourceHistoryBean::getResourceId, LcdpResourceHistoryBean::getResourceCategory, (v1, v2) -> v1));
      List<LcdpResourceLockBean> lockList = new ArrayList<>();
      resourceId2CategoryMap.forEach((k, v) -> {
         LcdpResourceLockBean resourceLock = new LcdpResourceLockBean();
         resourceLock.setId(ApplicationContextHelper.getNextIdentity());
         resourceLock.setResourceId(k.toString());
         resourceLock.setResourceCategory(v);
         lockList.add(resourceLock);
      });
      this.lcdpResourceLockService.getDao().insert(lockList);
      List<LcdpResourceBean> javaHistoryList = insertResourceList.stream()
         .filter(resource -> "java".equals(resource.getResourceCategory()))
         .collect(Collectors.toList());
      List<LcdpResourceServiceImpl.JavaSourceConfig> sourceConfigList = new ArrayList<>();
      javaHistoryList.forEach(java -> {
         String className = JavaCompilerManager.getClassName(java.getClassContent());
         String hintName = java.getResourceName();
         String sourceCode = java.getClassContent();
         sourceConfigList.add(new LcdpResourceServiceImpl.JavaSourceConfig(className, hintName, sourceCode, java));
      });
      this.insertServerScriptMethodAndRegister(sourceConfigList, importJavaRecord);
      return insertResourceList;
   }

   private void insertServerScriptMethodAndRegister(List<LcdpResourceServiceImpl.JavaSourceConfig> sourceConfigList, StringBuilder importJavaRecord) {
      if (!sourceConfigList.isEmpty()) {
         Map<String, String> sourceMap = new HashMap<>();
         sourceConfigList.forEach(s -> {
            String var10000 = sourceMap.put(s.getClassName(), s.getSourceCode());
         });
         LoadMultipleResult result = DynamicClassLoader.getInstance().loadSourceCode(sourceMap);
         if (result.getErrorMessageMap() != null) {
            result.getErrorMessageMap().forEach((k, v) -> {
               LcdpResourceServiceImpl.JavaSourceConfig sourceConfig = sourceConfigList.stream().filter(t -> k.equals(t.getClassName())).findAny().get();
               importJavaRecord.append("脚本加载异常：类名为" + sourceConfig.getHintName() + "，异常日志如下：" + v + "/r/n");
               log.error("============>脚本加载异常：类名为" + sourceConfig.getClassName() + "，异常日志如下：" + v);
               sourceConfigList.removeIf(t -> t.getClassName().equals(sourceConfig.getClassName()));
            });
            this.insertServerScriptMethodAndRegister(sourceConfigList, importJavaRecord);
         } else {
            List<LcdpServerScriptMethodBean> serverScriptMethodList = new ArrayList<>();
            List<Long> resourceIdList = new ArrayList<>();
            Map<Long, Method[]> methodMap = sourceConfigList.stream()
               .collect(
                  Collectors.toMap(
                     LcdpResourceServiceImpl.JavaSourceConfig::getResourceId,
                     his -> DynamicClassLoader.getInstance().loadSourceCode(his.getSourceCode()).getMethods()
                  )
               );

            for (LcdpResourceServiceImpl.JavaSourceConfig java : sourceConfigList) {
               for (Method method : (List<Method>)Arrays.stream(methodMap.get(java.getResourceId()))
                  .filter(methodx -> methodx.getAnnotation(Mapping.class) != null)
                  .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Method::getName))), ArrayList::new))) {
                  LcdpServerScriptMethodBean serverScriptMethod = new LcdpServerScriptMethodBean();
                  Mapping annotation = method.getAnnotation(Mapping.class);
                  String methodName = method.getName();
                  String methodPath = java.getPath() + "." + methodName;
                  serverScriptMethod.setId(ApplicationContextHelper.getNextIdentity());
                  serverScriptMethod.setServerScriptId(java.getResourceId());
                  serverScriptMethod.setMethodName(methodName);
                  serverScriptMethod.setMethodPath(methodPath);
                  serverScriptMethod.setMethodDesc(annotation.value());
                  serverScriptMethod.setMappingType(annotation.type().name());
                  serverScriptMethod.setMethodFlag("normal");
                  serverScriptMethod.setMethodCreatedById(LocalContextHelper.getLoginUserId());
                  resourceIdList.add(java.getResourceId());
                  serverScriptMethodList.add(serverScriptMethod);
               }
            }

            result.getClassMap().forEach((k, c) -> {
               LcdpResourceServiceImpl.JavaSourceConfig sourceConfig = sourceConfigList.stream().filter(t -> k.equals(t.getClassName())).findAny().orElse(null);
               if (sourceConfig != null) {
                  String beanName = sourceConfig.getClassName().toLowerCase();
                  SpringUtils.registerBean(beanName, (Class<?>)c);
                  LcdpResourceBean resource = sourceConfig.getResource();
                  this.stringRedisTemplate.convertAndSend(LcdpMapperUtils.LCDP_SYNC_SCRIPT_TOPIC, resource.getClassContent());
                  LcdpResourceCache.put(resource.getPath(), resource);

                  try {
                     CodeHintsManager.register(sourceConfig.getHintName(), SpringUtils.getBean(beanName).getClass());
                  } catch (Exception var9x) {
                     importJavaRecord.append("脚本提醒加载异常：类名为" + sourceConfig.getHintName() + "，异常日志如下：" + var9x.getMessage() + "/r/n");
                     log.error("============>脚本提醒加载异常：类名为" + sourceConfig.getClassName() + "，异常日志如下：" + var9x.getMessage());
                  }
               }
            });
            resourceIdList = resourceIdList.stream().distinct().collect(Collectors.toList());
            int batchSize = this.dialect.getMaxParamQty();
            List<LcdpServerScriptMethodBean> deleteServerScriptMethodList = new ArrayList<>();
            int numBatches = (int)Math.ceil((double)resourceIdList.size() / batchSize);

            for (int i = 0; i < numBatches; i++) {
               int start = i * batchSize;
               int end = Math.min(start + batchSize, resourceIdList.size());
               List<Long> batchIds = resourceIdList.subList(start, end);
               List<LcdpServerScriptMethodBean> methodList = this.serverScriptMethodService
                  .getDao()
                  .selectListByOneColumnValues(batchIds, "SERVERSCRIPTID", Arrays.asList("ID"));
               deleteServerScriptMethodList.addAll(methodList);
            }

            if (!deleteServerScriptMethodList.isEmpty()) {
               List<Long> deleteIdList = deleteServerScriptMethodList.stream().map(LcdpServerScriptMethodBean::getId).collect(Collectors.toList());
               this.serverScriptMethodService.getDao().deleteByIdList(deleteIdList);
            }

            this.serverScriptMethodService.getDao().insert(serverScriptMethodList);
         }
      }
   }

   private void dealHistoryAndPageCompData(
      LcdpResourceBean script,
      List<LcdpResourceHistoryBean> insertResourceHistoryList,
      Map<Long, List<LcdpModulePageCompBean>> resourceId2PageCompMap,
      Map<Long, List<LcdpModulePageI18nBean>> resourceId2PageI18nMap,
      Map<Long, List<LcdpPageI18nCodeBean>> resourceId2PageDependentI18nMap,
      Long resourceId,
      List<LcdpModulePageCompBean> insertMoudlePageCompList,
      List<LcdpModulePageI18nBean> insertPageI18nList,
      List<LcdpPageI18nCodeBean> insertPageDependentI18nList
   ) {
      LcdpResourceHistoryBean resourceHistory = new LcdpResourceHistoryBean();
      BeanUtils.copyProperties(script, resourceHistory, LcdpConstant.COPY_IGNORE_PROPERTIES);
      resourceHistory.setId(ApplicationContextHelper.getNextIdentity());
      resourceHistory.setResourceId(script.getId());
      resourceHistory.setVersion(script.getEffectVersion());
      resourceHistory.setSubmitFlag("yes");
      resourceHistory.setEffectFlag("yes");
      EntityHelper.assignCreatedElement(resourceHistory);
      insertResourceHistoryList.add(resourceHistory);
      if ("view".equals(script.getResourceCategory())) {
         List<LcdpModulePageCompBean> modulePageCompList = resourceId2PageCompMap.get(resourceId);
         List<LcdpModulePageI18nBean> modulePageI18nList = resourceId2PageI18nMap.get(resourceId);
         List<LcdpPageI18nCodeBean> modulePageDependentI18nList = resourceId2PageDependentI18nMap.get(resourceId);
         if (!ObjectUtils.isEmpty(modulePageCompList)) {
            List<LcdpModulePageCompBean> insertPageCompList = this.generatePageComps(modulePageCompList);
            insertPageCompList.forEach(comp -> {
               comp.setModulePageId(script.getId());
               comp.setModulePageHistoryId(resourceHistory.getId());
            });
            insertMoudlePageCompList.addAll(insertPageCompList);
         }

         if (!ObjectUtils.isEmpty(modulePageI18nList)) {
            modulePageI18nList.forEach(i18n -> {
               i18n.setId(ApplicationContextHelper.getNextIdentity());
               i18n.setModulePageId(script.getId());
               i18n.setModulePageHistoryId(resourceHistory.getId());
               EntityHelper.assignCreatedElement(i18n);
            });
            insertPageI18nList.addAll(modulePageI18nList);
         }

         if (!ObjectUtils.isEmpty(modulePageDependentI18nList)) {
            modulePageDependentI18nList.forEach(dependence -> {
               dependence.setId(ApplicationContextHelper.getNextIdentity());
               dependence.setModulePageId(script.getId());
               dependence.setModulePageHistoryId(resourceHistory.getId());
               EntityHelper.assignCreatedElement(dependence);
            });
            insertPageDependentI18nList.addAll(modulePageDependentI18nList);
         }
      }
   }

   private List<LcdpResourceBean> dealOverwriteResourceData(
      Map<Long, List<LcdpModulePageCompBean>> resourceId2PageCompMap,
      Map<Long, List<LcdpModulePageI18nBean>> resourceId2PageI18nMap,
      Map<Long, List<LcdpPageI18nCodeBean>> resourceId2PageDependentI18nMap,
      List<LcdpResourceBean> currentResourceList,
      List<LcdpResourceBean> overwriteList,
      LcdpSubmitLogBean submitLog,
      LcdpSubmitLogBean autoSubmitLog,
      StringBuilder importJavaRecord
   ) {
      Map<String, LcdpResourceBean> path2OverwriteResourceMap = overwriteList.stream()
         .collect(Collectors.toMap(LcdpResourceBean::getPath, Function.identity(), (v1, v2) -> v2));
      List<Long> currentResourceIdList = currentResourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      List<LcdpResourceHistoryBean> currentResourceHistoryList = this.lcdpResourceHistoryService
         .getDao()
         .selectListByOneColumnValues(
            currentResourceIdList,
            "RESOURCEID",
            Arrays.asList("ID", "RESOURCEID", "VERSION", "SUBMITFLAG", "EFFECTFLAG", "PATH", "CREATEDTIME", "RESOURCENAME", "RESOURCECATEGORY", "RESOURCEDESC")
         );
      List<LcdpResourceHistoryBean> currentNoSubmitHisList = currentResourceHistoryList.stream()
         .filter(his -> "no".equals(his.getSubmitFlag()))
         .collect(Collectors.toList());
      List<LcdpResourceHistoryBean> currentEffectList = currentResourceHistoryList.stream()
         .filter(his -> "yes".equals(his.getEffectFlag()))
         .collect(Collectors.toList());
      currentEffectList.forEach(his -> his.setEffectFlag("no"));
      this.lcdpResourceHistoryService.getDao().update(currentEffectList, "EFFECTFLAG");
      currentNoSubmitHisList.forEach(his -> his.setSubmitFlag("yes"));
      Map<Long, Optional<LcdpResourceHistoryBean>> resourceId2VersionMap = currentResourceHistoryList.stream()
         .collect(Collectors.groupingBy(LcdpResourceHistoryBean::getResourceId, Collectors.maxBy(Comparator.comparing(LcdpResourceHistoryBean::getVersion))));
      currentResourceList.forEach(resource -> {
         if (resourceId2VersionMap.get(resource.getId()) != null) {
            resource.setEffectVersion(resourceId2VersionMap.get(resource.getId()).get().getVersion());
         }
      });
      this.getDao().update(currentResourceList, new String[]{"EFFECTVERSION"});
      this.resourceCheckoutRecordService.unCheckoutResource(currentResourceList);
      this.lcdpResourceHistoryService.getDao().update(currentNoSubmitHisList, "SUBMITFLAG");
      this.lcdpResourceLockService.unLock(currentResourceIdList.stream().map(String::valueOf).collect(Collectors.toList()));
      List<LcdpResourceVersionBean> hisResourceVersionList = currentNoSubmitHisList.stream().map(his -> {
         LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
         resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
         resourceVersion.setResourceId(his.getResourceId().toString());
         resourceVersion.setVersion(his.getVersion());
         resourceVersion.setLogId(autoSubmitLog.getId());
         resourceVersion.setEditTime(his.getCreatedTime());
         resourceVersion.setSubmitTime(LocalDateTime.now());
         resourceVersion.setResourceName(his.getResourceName());
         resourceVersion.setResourceCategory(LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get(his.getResourceCategory()));
         resourceVersion.setEffectFlag("no");
         resourceVersion.setCommit(autoSubmitLog.getCommit());
         resourceVersion.setResourceAction(his.getVersion() == 1L ? "new" : "update");
         resourceVersion.setResourcePath(his.getPath().replaceAll("\\.", "/"));
         String[] path = his.getPath().split("\\.");
         resourceVersion.setCategoryName(path[0]);
         resourceVersion.setModuleName(path[1]);
         return resourceVersion;
      }).collect(Collectors.toList());
      List<LcdpResourceVersionBean> updateResourceVersionList = this.lcdpResourceVersionService
         .getDao()
         .selectListByOneColumnValues(currentResourceIdList.stream().map(String::valueOf).collect(Collectors.toList()), "RESOURCEID", Arrays.asList("ID"));
      updateResourceVersionList.forEach(version -> version.setEffectFlag("no"));
      this.lcdpResourceVersionService.getDao().update(updateResourceVersionList, "EFFECTFLAG");
      this.lcdpResourceVersionService.getDao().insert(hisResourceVersionList);
      List<LcdpResourceHistoryBean> insertHistoryList = new ArrayList<>();
      List<LcdpModulePageCompBean> insertPageCompsList = new ArrayList<>();
      List<LcdpModulePageI18nBean> insertPageI18nList = new ArrayList<>();
      List<LcdpPageI18nCodeBean> insertPageDependentI18nList = new ArrayList<>();

      for (LcdpResourceBean resource : currentResourceList) {
         LcdpResourceBean overWriteResource = path2OverwriteResourceMap.get(resource.getPath());
         resource.setEffectVersion(resource.getEffectVersion() + 1L);
         String content = overWriteResource.getContent();
         resource.setContent(content);
         resource.setResourceDesc(overWriteResource.getResourceDesc());
         if ("java".equals(resource.getResourceCategory()) && !StringUtils.isEmpty(overWriteResource.getClassContent())) {
            String classContent = this.replaceClassName(
               resource.getPath(), overWriteResource.getClassContent(), resource.getResourceName(), "v" + resource.getEffectVersion(), null
            );
            resource.setClassContent(classContent);
            String className = JavaCompilerManager.getClassName(classContent);
            resource.setClassName(className);
         }

         this.dealHistoryAndPageCompData(
            resource,
            insertHistoryList,
            resourceId2PageCompMap,
            resourceId2PageI18nMap,
            resourceId2PageDependentI18nMap,
            overWriteResource.getId(),
            insertPageCompsList,
            insertPageI18nList,
            insertPageDependentI18nList
         );
      }

      this.getDao().update(currentResourceList, new String[]{"EFFECTVERSION", "CONTENT", "CLASSNAME", "CLASSCONTENT", "RESOURCEDESC"});
      this.lcdpResourceHistoryService.getDao().insert(insertHistoryList);
      this.lcdpModulePageCompService.getDao().insert(insertPageCompsList);
      this.lcdpModulePageI18nService.getDao().insert(insertPageI18nList);
      this.lcdpPageI18nCodeService.getDao().insert(insertPageDependentI18nList);
      this.lcdpResourceVersionService.insertByHistoryResourceList(insertHistoryList, submitLog);
      List<LcdpResourceBean> javaList = currentResourceList.stream()
         .filter(resource -> "java".equals(resource.getResourceCategory()))
         .collect(Collectors.toList());
      List<LcdpResourceServiceImpl.JavaSourceConfig> sourceConfigList = new ArrayList<>();
      javaList.forEach(java -> {
         String classNamex = JavaCompilerManager.getClassName(java.getClassContent());
         String hintName = java.getResourceName();
         String sourceCode = java.getClassContent();
         sourceConfigList.add(new LcdpResourceServiceImpl.JavaSourceConfig(classNamex, hintName, sourceCode, java));
      });
      this.insertServerScriptMethodAndRegister(sourceConfigList, importJavaRecord);
      LcdpResourceCache.evictPageI18nList(insertPageI18nList.stream().map(LcdpModulePageI18nBean::getModulePageId).collect(Collectors.toList()));
      return currentResourceList;
   }

   private void importSysCode(
      List<CoreCodeCategoryBean> coreCodeCategoryList, List<CoreCodeBean> codeList, List<CoreAdminSelectConfigBean> coreAdminSelectConfigList
   ) {
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_CODE_NAME_OVERRIDE");
      if (!coreCodeCategoryList.isEmpty()) {
         List<CoreCodeCategoryBean> insertCodeCategoryList = new ArrayList<>();
         List<CoreCodeCategoryBean> existCategoryList = new ArrayList<>();
         List<CoreCodeCategoryBean> notExistCategoryList = new ArrayList<>();
         List<CoreAdminSelectConfigBean> insertAdminSelectConfigList = new ArrayList<>();
         List<CoreCodeBean> insertCodeList = new ArrayList<>();
         List<CoreCodeBean> updateCodeList = new ArrayList<>();
         List<String> categoryIdList = coreCodeCategoryList.stream().map(CoreCodeCategoryBean::getId).collect(Collectors.toList());
         List<CoreCodeCategoryBean> currentCodeCategoryList = this.coreCodeCategoryService.selectListByIds(categoryIdList);
         Map<String, CoreCodeCategoryBean> currentCategoryMap = currentCodeCategoryList.stream()
            .collect(Collectors.toMap(CoreCodeCategoryBean::getId, Function.identity()));
         coreCodeCategoryList.forEach(cc -> {
            if (currentCategoryMap.get(cc.getId()) != null) {
               existCategoryList.add(currentCategoryMap.get(cc.getId()));
            } else {
               notExistCategoryList.add(cc);
            }
         });
         Map<String, List<CoreCodeBean>> codeMap = codeList.stream().collect(Collectors.groupingBy(CoreCodeBean::getCodeCategoryId));
         if (!notExistCategoryList.isEmpty()) {
            insertCodeCategoryList.addAll(notExistCategoryList);
            notExistCategoryList.forEach(nc -> {
               if (!ObjectUtils.isEmpty(codeMap.get(nc.getId()))) {
                  insertCodeList.addAll(codeMap.get(nc.getId()));
               }
            });
         }

         if (!existCategoryList.isEmpty()) {
            List<String> existCategoryIdList = existCategoryList.stream().map(CoreCodeCategoryBean::getId).collect(Collectors.toList());
            List<CoreCodeBean> existCodeList = this.coreCodeService
               .selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", existCategoryIdList).filter(MatchPattern.OR), new Order[0]);
            Map<String, List<CoreCodeBean>> existCodeMap = existCodeList.stream().collect(Collectors.groupingBy(CoreCodeBean::getCodeCategoryId));
            existCodeMap.forEach(
               (existCategory, existCode) -> {
                  Map<String, List<CoreCodeBean>> tempExistCodeMap = existCode.stream()
                     .filter(code -> !StringUtils.isEmpty(code.getCode()))
                     .collect(Collectors.groupingBy(CoreCodeBean::getCode));
                  List<CoreCodeBean> sourceCodeList = codeMap.get(existCategory);
                  if (!ObjectUtils.isEmpty(sourceCodeList)) {
                     sourceCodeList.forEach(
                        source -> {
                           if (tempExistCodeMap.get(source.getCode()) == null) {
                              insertCodeList.add(source);
                           } else if ((queryCoreConstant == null || StringUtils.equals(queryCoreConstant.getConstantValue(), "1"))
                              && !StringUtils.equals(tempExistCodeMap.get(source.getCode()).get(0).getCodeName(), source.getCodeName())) {
                              tempExistCodeMap.get(source.getCode()).forEach(c -> c.setCodeName(source.getCodeName()));
                              updateCodeList.addAll(tempExistCodeMap.get(source.getCode()));
                           }
                        }
                     );
                  }
               }
            );
         }

         List<String> categoryList = coreAdminSelectConfigList.stream().map(CoreAdminSelectConfigBean::getCategory).collect(Collectors.toList());
         Map<String, CoreAdminSelectConfigBean> category2AdminSelectConfigMap = coreAdminSelectConfigList.stream()
            .collect(Collectors.toMap(CoreAdminSelectConfigBean::getCategory, Function.identity(), (v1, v2) -> v2));
         List<CoreAdminSelectConfigBean> exitAdminSelectConfigList = this.coreAdminSelectConfigService
            .selectListByFilter(SearchFilter.instance().match("category", categoryList).filter(MatchPattern.OR), new Order[0]);
         category2AdminSelectConfigMap.forEach(
            (category, selectConfig) -> {
               if (exitAdminSelectConfigList.stream().noneMatch(select -> StringUtils.equals(select.getCategory(), category))) {
                  insertAdminSelectConfigList.add(selectConfig);
               } else if (queryCoreConstant == null || StringUtils.equals(queryCoreConstant.getConstantValue(), "1")) {
                  exitAdminSelectConfigList.stream()
                     .filter(select -> StringUtils.equals(select.getCategory(), category))
                     .forEach(select -> select.setName(selectConfig.getName()));
               }
            }
         );
         insertCodeList.forEach(code -> code.setId(ApplicationContextHelper.getNextIdentity()));
         this.coreCodeCategoryService.getDao().insert(insertCodeCategoryList);
         this.coreCodeService.getDao().insert(insertCodeList);
         this.coreCodeService.getDao().update(updateCodeList, "CODENAME");
         this.coreAdminSelectConfigService.getDao().insert(insertAdminSelectConfigList);
         this.coreAdminSelectConfigService.getDao().update(exitAdminSelectConfigList, "name");
      }
   }

   private void importSysI18n(List<CoreI18nMessageBean> coreI18nMessageList) {
      List<String> codeList = coreI18nMessageList.stream().map(coreI18nMessageBean -> coreI18nMessageBean.getExt$Item("CODE")).collect(Collectors.toList());
      List<CoreI18nMessageBean> coreI18nMessageBeanList = this.coreI18nMessageService
         .selectListByFilter(SearchFilter.instance().match("CODE", codeList).filter(MatchPattern.OR), new Order[0]);
      Map<String, List<CoreI18nMessageBean>> coreI18nMap = new HashMap<>();
      List<CoreI18nBean> insertCoreI18nList = new ArrayList<>();
      List<CoreI18nMessageBean> insertCoreI18nMessageList = new ArrayList<>();
      List<CoreI18nMessageBean> updateCoreI18nMessageList = new ArrayList<>();
      coreI18nMessageBeanList.forEach(message -> {
         List<CoreI18nMessageBean> thisI18nMessageList = coreI18nMap.get(message.getExt$Item("CODE"));
         if (thisI18nMessageList == null) {
            thisI18nMessageList = new ArrayList<>();
            coreI18nMap.put(message.getExt$Item("CODE"), thisI18nMessageList);
         }

         thisI18nMessageList.add(message);
      });
      coreI18nMessageList.forEach(
         message -> {
            String code = message.getExt$Item("CODE");
            List<CoreI18nMessageBean> thisI18nMessageList = coreI18nMap.get(code);
            if (thisI18nMessageList == null) {
               CoreI18nBean i18nBean = new CoreI18nBean();
               i18nBean.setId(ApplicationContextHelper.getNextIdentity());
               i18nBean.setCode(code);
               i18nBean.setDescription(message.getMessage());
               i18nBean.setDefaultMessage(message.getMessage());
               insertCoreI18nList.add(i18nBean);
               List<CoreI18nMessageBean> var10 = new ArrayList();
               coreI18nMap.put(code, var10);
               CoreI18nMessageBean coreI18nMessageBean = new CoreI18nMessageBean();
               coreI18nMessageBean.setId(ApplicationContextHelper.getNextIdentity());
               coreI18nMessageBean.setI18nConfigId(message.getI18nConfigId());
               coreI18nMessageBean.setI18nId(i18nBean.getId());
               coreI18nMessageBean.setMessage(message.getMessage());
               insertCoreI18nMessageList.add(coreI18nMessageBean);
            } else {
               CoreI18nMessageBean coreI18nMessage = thisI18nMessageList.stream()
                  .filter(thisMessage -> StringUtils.equals(thisMessage.getI18nConfigId(), message.getI18nConfigId()))
                  .findFirst()
                  .orElse(null);
               if (coreI18nMessage == null) {
                  CoreI18nMessageBean anotherCoreI18nMessage = thisI18nMessageList.stream().findAny().orElse(null);
                  if (anotherCoreI18nMessage != null) {
                     CoreI18nMessageBean coreI18nMessageBean = new CoreI18nMessageBean();
                     coreI18nMessageBean.setId(ApplicationContextHelper.getNextIdentity());
                     coreI18nMessageBean.setI18nConfigId(message.getI18nConfigId());
                     coreI18nMessageBean.setI18nId(anotherCoreI18nMessage.getI18nId());
                     coreI18nMessageBean.setMessage(message.getMessage());
                     insertCoreI18nMessageList.add(coreI18nMessageBean);
                  }
               } else {
                  coreI18nMessage.setMessage(message.getMessage());
                  updateCoreI18nMessageList.add(coreI18nMessage);
               }
            }
         }
      );
      this.coreI18nService.getDao().insert(insertCoreI18nList);
      this.coreI18nMessageService.getDao().insert(insertCoreI18nMessageList);
      this.coreI18nMessageService.updateIfChanged(updateCoreI18nMessageList);
   }

   private void importSysClientJs(Map<String, String> fileMap) {
      String sysClientJs = fileMap.get("sysClientJs");
      if (!StringUtils.isEmpty(sysClientJs)) {
         LcdpGlobalConfigBean sysClientJsBean = JsonUtils.parse(sysClientJs, LcdpGlobalConfigBean.class);
         PersistableHelper.resetBasicProperties(sysClientJsBean);
         this.lcdpGlobalConfigService.submit(sysClientJsBean);
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

   private String createExportFile(LcdpResourceExportDTO exportDTO, LcdpResourceCheckoutConfigDTO checkoutConfigDTO, LcdpCheckoutRecordBean checkoutRecord) {
      LcdpExportLogFileDTO exportLogFileDTO = new LcdpExportLogFileDTO();
      exportLogFileDTO.setTreeNodeDTOList(exportDTO.getTreeNodeDtoList());
      exportLogFileDTO.setLog(exportDTO.getExportLog());
      exportLogFileDTO.setCheckoutRecordNo(checkoutRecord.getCheckoutNo());
      String treeStr = JSONObject.toJSONString(exportLogFileDTO);
      LocalDateTime now = LocalDateTime.now();
      String uuid = StringUtils.randomUUID(16);
      String zipName = "export_"
         + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
         + "_"
         + LocalContextHelper.getLoginUserId()
         + ".swdp";
      FilePathDTO folderFilePath = FilePathDTO.of(FileScope.temp.name(), now, uuid, "resource");
      FilePathDTO zipFilePath = FilePathDTO.of(FileScope.temp.name(), now, uuid, zipName);
      Path path = FilePathManager.getLocalPath(folderFilePath);
      String resourcePath = path.toString() + File.separator;
      String sysCodePath = FilePathManager.getLocalPath(FilePathDTO.of(FileScope.temp.name(), now, uuid, "sysCode")).toString() + File.separator;
      String menuPath = FilePathManager.getLocalPath(FilePathDTO.of(FileScope.temp.name(), now, uuid, "menu")).toString() + File.separator;
      String globalConfigPath = FilePathManager.getLocalPath(FilePathDTO.of(FileScope.temp.name(), now, uuid, "globalConfig")).toString() + File.separator;
      String checkoutConfigPath = FilePathManager.getLocalPath(FilePathDTO.of(FileScope.temp.name(), now, uuid, "checkoutConfig")).toString() + File.separator;
      String i18nPath = FilePathManager.getLocalPath(FilePathDTO.of(FileScope.temp.name(), now, uuid, "i18n")).toString() + File.separator;
      File zip = FilePathManager.getLocalPath(zipFilePath).toFile();
      File exportLog = new File(path.getParent() + File.separator + "exportLog");
      FileUtils.write(exportLog, EncryptUtils.base64Encode(treeStr));
      Map<Long, List<LcdpModulePageCompBean>> modulePageId2CompMap = new HashMap<>();
      Map<Long, List<LcdpModulePageI18nBean>> modulePageId2I18nMap = null;
      Map<Long, List<LcdpPageI18nCodeBean>> modulePageId2I18nDependenceMap = null;
      List<CoreI18nMessageBean> coreI18nMessageList = new ArrayList<>();
      Map<String, List<String>> type2ParamMap = new HashMap<>();
      List<LcdpResourceBean> viewList = exportDTO.getExportResourceList()
         .stream()
         .filter(his -> "view".equals(his.getResourceCategory()))
         .collect(Collectors.toList());
      List<Long> idList = viewList.stream().map(his -> Long.valueOf(his.getExt$Item("resourcehistoryid"))).collect(Collectors.toList());
      LcdpGlobalConfigBean sysClientJs = this.lcdpGlobalConfigService.selectConfigContent("SYS_CLIENT_JS");
      List<CoreMenuBean> menuList = new ArrayList<>();
      if (!idList.isEmpty()) {
         List<LcdpModulePageCompBean> modulePageCompList = this.lcdpModulePageCompService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", idList).filter(MatchPattern.OR), new Order[0]);
         List<String> configList = modulePageCompList.stream()
            .map(comp -> comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend()))
            .collect(Collectors.toList());
         type2ParamMap = LcdpPageCompConfigAnalyseHelper.analyseConfig(configList);
         modulePageId2CompMap = modulePageCompList.stream().collect(Collectors.groupingBy(LcdpModulePageCompBean::getModulePageId));
         List<Long> resourceIdList = viewList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         menuList = this.coreMenuService.selectListByFilter(SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR), new Order[0]);
         List<LcdpModulePageI18nBean> pageI18nList = this.lcdpModulePageI18nService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", idList).filter(MatchPattern.OR), new Order[0]);
         modulePageId2I18nMap = pageI18nList.stream().collect(Collectors.groupingBy(LcdpModulePageI18nBean::getModulePageId));
         List<LcdpPageI18nCodeBean> pageI18nCodeList = this.lcdpPageI18nCodeService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", idList).filter(MatchPattern.OR), new Order[0]);
         modulePageId2I18nDependenceMap = pageI18nCodeList.stream().collect(Collectors.groupingBy(LcdpPageI18nCodeBean::getModulePageId));
         Set<String> codeSet = pageI18nCodeList.stream().map(LcdpPageI18nCodeBean::getCode).collect(Collectors.toSet());
         if (!codeSet.isEmpty()) {
            ArrayList<String> codeList = new ArrayList<>(codeSet);
            coreI18nMessageList.addAll(
               this.coreI18nMessageService.selectListByFilter(SearchFilter.instance().match("CODE", codeList).filter(MatchPattern.OR), new Order[0])
            );
         }
      }

      for (LcdpResourceBean resource : exportDTO.getExportResourceList()) {
         FileUtils.write(new File(resourcePath + resource.getId()), EncryptUtils.base64Encode(JSONObject.toJSONString(resource)));
         if ("view".equals(resource.getResourceCategory())) {
            if (!ObjectUtils.isEmpty(modulePageId2CompMap.get(resource.getId()))) {
               FileUtils.write(
                  new File(resourcePath + resource.getId() + "view"),
                  EncryptUtils.base64Encode(JSONObject.toJSONString(modulePageId2CompMap.get(resource.getId())))
               );
            }

            if (!ObjectUtils.isEmpty(modulePageId2I18nMap.get(resource.getId()))) {
               FileUtils.write(
                  new File(resourcePath + resource.getId() + "i18n"),
                  EncryptUtils.base64Encode(JSONObject.toJSONString(modulePageId2I18nMap.get(resource.getId())))
               );
            }

            if (!ObjectUtils.isEmpty(modulePageId2I18nDependenceMap.get(resource.getId()))) {
               FileUtils.write(
                  new File(resourcePath + resource.getId() + "sysI18nDependence"),
                  EncryptUtils.base64Encode(JSONObject.toJSONString(modulePageId2I18nDependenceMap.get(resource.getId())))
               );
            }
         }
      }

      for (LcdpTableDTO tableDTO : exportDTO.getTableDTOList()) {
         FileUtils.write(new File(resourcePath + tableDTO.getTableName()), EncryptUtils.base64Encode(JSONObject.toJSONString(tableDTO)));
      }

      for (LcdpViewBean view : exportDTO.getViewList()) {
         FileUtils.write(new File(resourcePath + view.getViewName()), EncryptUtils.base64Encode(JSONObject.toJSONString(view)));
      }

      if (!type2ParamMap.isEmpty() && !ObjectUtils.isEmpty(type2ParamMap.get("select"))) {
         List<String> selectParam = type2ParamMap.get("select");
         List<CoreCodeCategoryBean> coreCodeCategoryList = this.coreCodeCategoryService.selectListByIds(selectParam);
         List<CoreCodeBean> codeList = this.coreCodeService
            .selectListByFilter(SearchFilter.instance().match("codeCategoryId", selectParam).filter(MatchPattern.OR), new Order[0]);
         List<CoreAdminSelectConfigBean> coreAdminSelectConfigList = this.coreAdminSelectConfigService
            .selectListByFilter(SearchFilter.instance().match("category", selectParam).filter(MatchPattern.OR), new Order[0]);
         coreCodeCategoryList = coreCodeCategoryList.stream()
            .collect(
               Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CoreCodeCategoryBean::getId))), ArrayList::new)
            );
         FileUtils.write(new File(sysCodePath + "codeCategory"), EncryptUtils.base64Encode(JSONObject.toJSONString(coreCodeCategoryList)));
         FileUtils.write(new File(sysCodePath + "code"), EncryptUtils.base64Encode(JSONObject.toJSONString(codeList)));
         FileUtils.write(new File(sysCodePath + "adminSelectConfig"), EncryptUtils.base64Encode(JSONObject.toJSONString(coreAdminSelectConfigList)));
      }

      if (!menuList.isEmpty()) {
         FileUtils.write(new File(menuPath + "menu"), EncryptUtils.base64Encode(JSONObject.toJSONString(menuList)));
      }

      if (StringUtils.equals(checkoutConfigDTO.getExportSysClientJsFlag(), "1") && !ObjectUtils.isEmpty(sysClientJs)) {
         FileUtils.write(new File(globalConfigPath + "sysClientJs"), EncryptUtils.base64Encode(JSONObject.toJSONString(sysClientJs)));
      }

      if (!coreI18nMessageList.isEmpty()) {
         FileUtils.write(new File(i18nPath + "i18n"), EncryptUtils.base64Encode(JSONObject.toJSONString(coreI18nMessageList)));
      }

      FileUtils.write(new File(checkoutConfigPath + "checkoutConfig"), EncryptUtils.base64Encode(JSONObject.toJSONString(checkoutConfigDTO)));
      ArchiveUtils.zip(
         zip, new File(resourcePath), new File(sysCodePath), exportLog, new File(globalConfigPath), new File(checkoutConfigPath), new File(i18nPath)
      );
      CoreFileBean coreFileBean = new CoreFileBean();
      Long key = ApplicationContextHelper.getNextIdentity();
      coreFileBean.setId(key);
      coreFileBean.setName(zipName);
      coreFileBean.setFileExt("swdp");
      coreFileBean.setTargetId(this.checkoutRecordService.getDao().getTable() + "$" + checkoutRecord.getId());
      coreFileBean.setScope(FileScope.temp.name());
      coreFileBean.setValidTimeFrom(null);
      coreFileBean.setValidTimeTo(null);
      coreFileBean.setMd5Name(uuid);
      coreFileBean.setTimes(0L);
      coreFileBean.setCreatedTime(now);
      coreFileBean.setPermanent("1");
      coreFileBean.setVersion(1L);
      this.coreFileService.getDao().insert(coreFileBean);
      this.fileManager.upload(coreFileBean, zip.toPath());
      return this.fileManager.getAbsoluteDownloadUrl(coreFileBean);
   }

   private List<LcdpResourceBean> getExportedResources(List<Long> resourceIdList) {
      List<LcdpResourceBean> sourceResourceList = this.selectListByIds(resourceIdList);
      Map<Long, Long> parentIdMap = sourceResourceList.stream().collect(Collectors.toMap(LcdpResourceBean::getId, LcdpResourceBean::getParentId));
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR), new Order[0]);
      new ArrayList();
      Map<Long, List<LcdpResourceHistoryBean>> resourceId2HistoryMap = resourceHistoryList.stream()
         .collect(Collectors.groupingBy(LcdpResourceHistoryBean::getResourceId));
      List<LcdpResourceHistoryBean> effectList = new ArrayList<>();
      sourceResourceList.forEach(
         resource -> {
            LcdpResourceHistoryBean effectHistory = resourceId2HistoryMap.get(resource.getId())
               .stream()
               .filter(history -> resource.getEffectVersion() == history.getVersion())
               .findFirst()
               .orElse(null);
            if (effectHistory != null) {
               effectList.add(effectHistory);
            }
         }
      );
      List<LcdpResourceBean> resourceList = new ArrayList<>();
      List<LcdpResourceBean> scriptList = effectList.stream().map(his -> {
         LcdpResourceBean resource = new LcdpResourceBean();
         BeanUtils.copyProperties(his, resource);
         resource.setId(his.getResourceId());
         resource.setOrderNo(1L);
         resource.setExt$Item("resourcehistoryid", his.getId().toString());
         resource.setParentId(parentIdMap.get(his.getResourceId()));
         return resource;
      }).collect(Collectors.toList());
      List<Long> moduleIdList = scriptList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
      List<LcdpResourceBean> mooduleList = this.selectListByIds(moduleIdList);
      List<Long> categoryIdList = mooduleList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
      List<LcdpResourceBean> categoryList = this.selectListByIds(categoryIdList);
      resourceList.addAll(scriptList);
      resourceList.addAll(mooduleList);
      resourceList.addAll(categoryList);
      return resourceList;
   }

   private String[] firstUppercase(String tableName) {
      String newName = "";
      String firstNewName = "";

      for (int a = 0; a < tableName.length(); a++) {
         char chr = tableName.charAt(a);
         if (a == 0) {
            newName = newName + chr;
            firstNewName = firstNewName + String.valueOf(chr).toUpperCase();
         } else if ("_".equals(String.valueOf(chr))) {
            newName = newName + String.valueOf(tableName.charAt(a + 1)).toUpperCase();
            firstNewName = firstNewName + String.valueOf(tableName.charAt(a + 1)).toUpperCase();
            a++;
         } else {
            newName = newName + chr;
            firstNewName = firstNewName + chr;
         }
      }

      return new String[]{newName, firstNewName};
   }

   private void cutConfig(String sourceConfig, LcdpModulePageCompBean page) {
      if (sourceConfig.length() > 3000) {
         page.setConfig(sourceConfig.substring(0, 3000));
         page.setConfigExtend(sourceConfig.substring(3000));
      } else {
         page.setConfig(sourceConfig);
         page.setConfigExtend(null);
      }
   }

   private int findCharCount(String content, char findChar) {
      int num = 0;

      for (int i = 0; i < content.length(); i++) {
         if (content.charAt(i) == findChar) {
            num++;
         }
      }

      return num;
   }

   private void dealRenamePage(String oldPath, String newPath, List<LcdpModulePageCompBean> newModulePageCompList) {
      newModulePageCompList.forEach(comp -> {
         String config = StringUtils.isEmpty(comp.getConfigExtend()) ? comp.getConfig() : comp.getConfig() + comp.getConfigExtend();
         JSONObject configObject = JSON.parseObject(config);
         configObject.forEach((k, v) -> {
            if (v instanceof JSONObject) {
               JSONObject propertyObject = (JSONObject)v;

               for (String property : propertyObject.keySet()) {
                  Object value = propertyObject.get(property);
                  if (StringUtils.equals(property, "type") && StringUtils.equals(propertyObject.getString(property), "i18n")) {
                     if (!StringUtils.isEmpty(propertyObject.getString("i18nCode")) && propertyObject.getString("i18nCode").startsWith(oldPath)) {
                        String oldI18nCode = propertyObject.getString("i18nCode");
                        String newI18nCode = oldI18nCode.replaceAll(oldPath, newPath);
                        propertyObject.put("i18nCode", newI18nCode);
                     }
                     break;
                  }
               }
            }
         });
         this.cutConfig(configObject.toJSONString(), comp);
      });
   }

   private List<LcdpJavaStructureDTO> getClassStructureInfo(Class<?> clazz, String classContent) {
      Map<String, Object> mapper = new HashMap<>();
      Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> mapper.put(method.getName(), method));
      Arrays.stream(clazz.getDeclaredFields()).forEach(field -> mapper.put(field.getName(), field));
      String[] lines = classContent.split("\n");
      List<LcdpJavaStructureDTO> list = new ArrayList<>();
      int leftBraceNum = 0;
      int rightBraceNum = 0;

      for (int i = 0; i < lines.length; i++) {
         String line = lines[i];
         leftBraceNum += this.findCharCount(line, '{');
         rightBraceNum += this.findCharCount(line, '}');
         if (leftBraceNum != 0 && (leftBraceNum == rightBraceNum + 1 || line.trim().endsWith("{") && leftBraceNum == rightBraceNum + 2)) {
            String[] lineSplitParts = line.split("(\\s|\\()+");
            Object desc = null;

            for (String part : lineSplitParts) {
               if (mapper.get(part) != null) {
                  desc = mapper.get(part);
                  break;
               }
            }

            if (desc != null) {
               LcdpJavaStructureDTO structure = new LcdpJavaStructureDTO();
               if (desc instanceof Method) {
                  Method methodDescription = (Method)desc;
                  String returnType = methodDescription.getReturnType().getSimpleName();
                  List<String> paramTypes = Arrays.stream(methodDescription.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.toList());
                  structure.setType("method");
                  structure.setName(String.format("%s(%s): %s", methodDescription.getName(), StringUtils.join(paramTypes, ","), returnType));
                  structure.setLineNumber(i + 1);
               } else if (desc instanceof Field) {
                  Field description = (Field)desc;
                  String fieldTypeName = description.getType().getSimpleName();
                  structure.setType("field");
                  structure.setName(String.format("%s: %s", description.getName(), fieldTypeName));
                  structure.setLineNumber(i + 1);
               }

               list.add(structure);
            }
         }
      }

      return list;
   }

   private void getPackageImport(String content, List<String> importList) {
      List<String> partImportList = new ArrayList<>();
      String classContent = this.replaceScriptContent(content);

      try {
         JavaCompilerManager.compile(classContent);
      } catch (CompileException var12) {
         for (Object json : JSONObject.parseArray(var12.getMessage())) {
            JSONObject jsonObject = (JSONObject)json;
            String type = (String)jsonObject.get("type");
            String importContent = (String)jsonObject.get("content");
            if ("import".equals(type) && !importList.contains(importContent)) {
               partImportList.add(importContent);
               importList.add(importContent);
            }
         }

         if (partImportList.size() > 0) {
            this.getPackageImport(StringUtils.join(partImportList, "\n") + classContent, importList);
         }
      }
   }

   private File getClassFile(Class<?> clazz, String targetDirectory) throws IOException {
      String className = clazz.getName();
      String classPath = className.replace(".", "/") + ".class";
      ClassLoader classLoader = clazz.getClassLoader();
      InputStream is = classLoader == null ? ClassLoader.getSystemResourceAsStream(classPath) : classLoader.getResourceAsStream(classPath);
      File tempFile = new File(targetDirectory + "/" + className + ".class");
      FileOutputStream fos = null;
      if (is == null) {
         return null;
      } else {
         try {
            fos = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];

            int length;
            while ((length = is.read(buffer)) != -1) {
               fos.write(buffer, 0, length);
            }
         } catch (IOException var14) {
         } finally {
            is.close();
            if (fos != null) {
               fos.close();
            }
         }

         return tempFile;
      }
   }

   public String getJarPath() {
      try {
         Resource resource = this.resourceLoader.getResource("classpath:");
         String url = resource.getURL().toString();
         if (url.contains(".jar!")) {
            String jarPath = url.substring(url.indexOf("file:/") + 5, url.indexOf(".jar!") + 4);
            return new File(jarPath).getParent().replace("\\", "/");
         } else {
            return new File(resource.getFile().getPath()).getParent();
         }
      } catch (IOException var4) {
         return null;
      }
   }

   private boolean deleteDirectory(File directoryToBeDeleted) {
      File[] allContents = directoryToBeDeleted.listFiles();
      if (allContents != null) {
         for (File file : allContents) {
            if (!this.deleteDirectory(file)) {
               return false;
            }
         }
      }

      return directoryToBeDeleted.delete();
   }

   private String decompileClassBySimpleName(String simpleClassName) {
      Class<?> clazz = CodeHintsManager.getClassByName(simpleClassName.toLowerCase());
      if (clazz == null) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         String jarPath = this.getJarPath();
         File decompiledRootDirectory = new File(jarPath + "/decompiled-sources");
         File classRootDirectory = new File(jarPath + "/decompiled-classes");
         decompiledRootDirectory.mkdir();
         classRootDirectory.mkdir();

         try {
            File sourceClassFile = this.getClassFile(clazz, classRootDirectory.getPath());
            if (sourceClassFile != null) {
               Path sourceClassPath = sourceClassFile.toPath();
               Path decompiledRootPath = decompiledRootDirectory.toPath();
               DecompilationResult result = new FernflowerDecompiler().decompileClassFile(null, sourceClassPath, decompiledRootPath);

               for (Entry<String, String> entry : result.getDecompiledFiles().entrySet()) {
                  Files.lines(new File(entry.getValue()).toPath(), Charset.defaultCharset()).forEach(line -> sb.append(line).append("\n"));
               }
            }
         } catch (Exception var16) {
            throw new CheckedException(String.format(I18nHelper.getMessage("LCDP.EXCEPTION.DECOMPILE_ERROR"), simpleClassName));
         } finally {
            this.deleteDirectory(decompiledRootDirectory);
            this.deleteDirectory(classRootDirectory);
         }

         return sb.toString();
      }
   }

   private void addCompDocument(
      List<LcdpResourceBean> resourceList, List<LcdpResourceHistoryBean> resourceHistoryList, List<LcdpResourceDocumentBean> documentList
   ) {
      List<Long> resourceHistoryIdList = resourceHistoryList.stream()
         .filter(f -> "view".equals(f.getResourceCategory()))
         .map(e -> e.getId())
         .collect(Collectors.toList());

      for (LcdpModulePageCompBean historyComp : this.lcdpModulePageCompService
         .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistoryIdList).filter(MatchPattern.OR), new Order[0])) {
         LcdpResourceHistoryBean historyResource = resourceHistoryList.stream()
            .filter(f -> ObjectUtils.equals(f.getId(), historyComp.getModulePageHistoryId()))
            .findFirst()
            .orElse(new LcdpResourceHistoryBean());
         LcdpResourceBean resource = resourceList.stream()
            .filter(f -> ObjectUtils.equals(f.getId(), historyComp.getModulePageId()))
            .findFirst()
            .orElse(new LcdpResourceBean());
         String content = historyComp.getConfig() + (StringUtils.isEmpty(historyComp.getConfigExtend()) ? "" : historyComp.getConfigExtend());
         documentList.add(
            new LcdpResourceDocumentBean(
               resource.getId() + "$$" + historyComp.getId(),
               resource.getResourceName(),
               resource.getPath(),
               "comp",
               content,
               historyComp.getModulePageVersion(),
               "1",
               resource.getCreatedById()
            )
         );
         documentList.add(
            new LcdpResourceDocumentBean(
               historyResource.getId() + "$$" + historyComp.getId(),
               historyResource.getResourceName(),
               historyResource.getPath(),
               "comp",
               content,
               historyComp.getModulePageVersion(),
               "0",
               historyResource.getCreatedById()
            )
         );
      }
   }

   private void deleteCompDocumentById(List<String> resourceIdList, List<String> resourceHistoryIdList) {
      List<LcdpModulePageCompBean> resourceIdConditionList = resourceIdList.stream().map(id -> {
         LcdpModulePageCompBean lcdpModulePageCompBean = new LcdpModulePageCompBean();
         lcdpModulePageCompBean.setModulePageId(ConvertUtils.convert(id, Long.class, 0L));
         return lcdpModulePageCompBean;
      }).collect(Collectors.toList());
      List<LcdpModulePageCompBean> resourceHistoryIdConditionList = resourceHistoryIdList.stream().map(id -> {
         LcdpModulePageCompBean lcdpModulePageCompBean = new LcdpModulePageCompBean();
         lcdpModulePageCompBean.setModulePageHistoryId(ConvertUtils.convert(id, Long.class, 0L));
         return lcdpModulePageCompBean;
      }).collect(Collectors.toList());
      List<LcdpModulePageCompBean> resourceCompList = this.lcdpModulePageCompService
         .getDao()
         .selectListByOneColumnValues("MODULEPAGEID", resourceIdConditionList, ArrayUtils.asList("ID", "MODULEPAGEID"));
      List<LcdpModulePageCompBean> resourceHistoryCompList = this.lcdpModulePageCompService
         .getDao()
         .selectListByOneColumnValues("MODULEPAGEHISTORYID", resourceHistoryIdConditionList, ArrayUtils.asList("ID", "MODULEPAGEHISTORYID"));
      List<String> compIdList = new ArrayList<>();
      if (!resourceCompList.isEmpty()) {
         for (LcdpModulePageCompBean comp : resourceCompList) {
            String resourceId = resourceIdList.stream().filter(f -> ObjectUtils.equals(f, comp.getModulePageId() + "")).findFirst().get();
            compIdList.add(resourceId + "$$" + comp.getId());
         }
      }

      if (!resourceHistoryCompList.isEmpty()) {
         for (LcdpModulePageCompBean comp : resourceHistoryCompList) {
            String resourceHistoryId = resourceHistoryIdList.stream().filter(f -> ObjectUtils.equals(f, comp.getModulePageHistoryId() + "")).findFirst().get();
            compIdList.add(resourceHistoryId + "$$" + comp.getId());
         }
      }

      if (compIdList != null && compIdList.size() > 0) {
         LuceneIndexManager.deleteIndex(compIdList);
      }
   }

   private void deleteCompDocument(List<LcdpResourceBean> resourceList, List<LcdpResourceHistoryBean> resourceHistoryList) {
      List<String> resourceIdList = resourceList.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
      List<String> resourceHistoryIdList = resourceHistoryList.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
      this.deleteCompDocumentById(resourceIdList, resourceHistoryIdList);
   }

   private void rename(List<LcdpResourceHistoryBean> list, String newName) {
      for (LcdpResourceHistoryBean resourceHistory : list) {
         String path = resourceHistory.getPath();
         String resourceName = resourceHistory.getResourceName();
         int lastDotIndex = path.lastIndexOf(".");
         String newPath = path.substring(0, lastDotIndex + 1) + newName;
         if ("java".equals(resourceHistory.getResourceCategory())) {
            String content = resourceHistory.getContent();
            if (!StringUtils.isEmpty(content)) {
               String newContent = content.replaceFirst("public\\s+class\\s+" + resourceName, "public class " + newName);
               resourceHistory.setContent(newContent);
               String classContent = this.replaceScriptContent(this.replaceClassName(newPath, newContent, newName, null, "m1"));
               resourceHistory.setClassContent(classContent);
            }
         }

         if ("view".equals(resourceHistory.getResourceCategory())) {
            List<LcdpModulePageI18nBean> modulePageI18nList = this.lcdpModulePageI18nService
               .selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
            if (!ObjectUtils.isEmpty(modulePageI18nList)) {
               List<LcdpModulePageI18nBean> newPageI18nList = modulePageI18nList.stream().map(i18nBean -> {
                  LcdpModulePageI18nBean newModulePageI18n = new LcdpModulePageI18nBean();
                  BeanUtils.copyProperties(i18nBean, newModulePageI18n);
                  newModulePageI18n.setId(ApplicationContextHelper.getNextIdentity());
                  newModulePageI18n.setCode(StringUtils.replace(i18nBean.getCode(), path, newPath));
                  return newModulePageI18n;
               }).collect(Collectors.toList());
               this.lcdpModulePageI18nService.getDao().deleteBy(modulePageI18nList);
               this.lcdpModulePageI18nService.getDao().insert(newPageI18nList);
               List<LcdpModulePageCompBean> modulePageCompList = this.lcdpModulePageCompService
                  .selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
               this.dealRenamePage(path, newPath, modulePageCompList);
               this.lcdpModulePageCompService.getDao().update(modulePageCompList, "CONFIG", "CONFIGEXTEND");
            }
         }

         resourceHistory.setResourceName(newName);
         resourceHistory.setPath(newPath);
      }

      this.lcdpResourceHistoryService.updateIfChanged(list);
      list.stream().forEach(e -> {
         if ("java".equals(e.getResourceCategory())) {
            this.insertServerScriptMethod(e);
         }
      });
      List<LcdpResourceDocumentBean> updateDocumentList = new ArrayList<>();
      list.stream().forEach(e -> updateDocumentList.add(new LcdpResourceDocumentBean(e)));
      LuceneIndexManager.updateIndex(updateDocumentList);
   }

   private void operateCompDocument(LcdpResourceBean resource, LcdpResourceHistoryBean resourceHistory, String operateType) {
      List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
      if (resource != null && "view".equals(resource.getResourceCategory())) {
         for (LcdpModulePageCompBean comp : this.lcdpModulePageCompService.getDao().selectListByOneColumnValue(resource.getId(), "MODULEPAGEID")) {
            String content = comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend());
            documentList.add(
               new LcdpResourceDocumentBean(
                  resource.getId() + "$$" + comp.getId(),
                  resource.getResourceName(),
                  resource.getPath(),
                  "comp",
                  content,
                  comp.getModulePageVersion(),
                  "1",
                  resource.getCreatedById()
               )
            );
         }
      }

      if (resourceHistory != null && "view".equals(resourceHistory.getResourceCategory())) {
         for (LcdpModulePageCompBean comp : this.lcdpModulePageCompService.getDao().selectListByOneColumnValue(resourceHistory.getId(), "MODULEPAGEHISTORYID")) {
            String content = comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend());
            documentList.add(
               new LcdpResourceDocumentBean(
                  resourceHistory.getId() + "$$" + comp.getId(),
                  resourceHistory.getResourceName(),
                  resourceHistory.getPath(),
                  "comp",
                  content,
                  comp.getModulePageVersion(),
                  "0",
                  resourceHistory.getCreatedById()
               )
            );
         }
      }

      if ("update".equals(operateType)) {
         LuceneIndexManager.updateIndex(documentList);
      } else if ("add".equals(operateType)) {
         LuceneIndexManager.addIndex(documentList);
      }
   }

   private void savePageComps(Long id, List<LcdpModulePageCompBean> modulePageCompList) {
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(
            SearchFilter.instance()
               .match("RESOURCEID", id)
               .filter(MatchPattern.EQ)
               .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.SEQ)
               .match("SUBMITFLAG", "no")
               .filter(MatchPattern.SEQ),
            new Order[0]
         );
      LcdpResourceHistoryBean resourceHistory = resourceHistoryList.stream().findFirst().get();
      modulePageCompList.forEach(comp -> {
         comp.setModulePageId(resourceHistory.getResourceId());
         comp.setModulePageHistoryId(resourceHistory.getId());
         comp.setModulePageVersion(resourceHistory.getVersion());
         EntityHelper.assignCreatedElement(comp);
      });
      List<LcdpModulePageCompBean> deleteModulePageCompList = this.lcdpModulePageCompService
         .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistory.getId()).filter(MatchPattern.SEQ), new Order[0]);
      List<String> deleteIdList = deleteModulePageCompList.stream().map(LcdpModulePageCompBean::getId).collect(Collectors.toList());
      this.lcdpModulePageCompService.getDao().deleteByIdList(deleteIdList);

      for (LcdpModulePageCompBean pageComp : modulePageCompList) {
         pageComp.setConfigExtend(null);
         this.cutConfig(pageComp.getConfig(), pageComp);
      }

      this.lcdpModulePageCompService.getDao().insert(modulePageCompList);
   }

   private void savePageI18n(LcdpResourceHistoryBean resourceHistory, Map<String, Map<String, String>> modulePageI18n) {
      List<LcdpModulePageI18nBean> modulePageI18nBeanList = new ArrayList<>();
      modulePageI18n.forEach((code, i18Message) -> i18Message.forEach((locale, message) -> {
         LcdpModulePageI18nBean i18nMessage = new LcdpModulePageI18nBean();
         i18nMessage.setModulePageId(resourceHistory.getResourceId());
         i18nMessage.setModulePageHistoryId(resourceHistory.getId());
         i18nMessage.setId(ApplicationContextHelper.getNextIdentity());
         i18nMessage.setCode(code);
         i18nMessage.setI18nConfigId(locale);
         i18nMessage.setMessage(message);
         modulePageI18nBeanList.add(i18nMessage);
      }));
      List<LcdpModulePageI18nBean> deleteModulePageCompList = this.lcdpModulePageI18nService
         .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistory.getId()).filter(MatchPattern.SEQ), new Order[0]);
      List<Long> deleteIdList = deleteModulePageCompList.stream().map(LcdpModulePageI18nBean::getId).collect(Collectors.toList());
      this.lcdpModulePageI18nService.getDao().deleteByIdList(deleteIdList);
      this.lcdpModulePageI18nService.getDao().insert(modulePageI18nBeanList);
   }

   private void savePageDependentI18n(LcdpResourceHistoryBean resourceHistory, List<String> pageDependentI18n) {
      List<LcdpPageI18nCodeBean> newPageI18nCodeList = new ArrayList<>();
      List<Long> deletePageI18nCodeIdList = new ArrayList<>();
      List<LcdpPageI18nCodeBean> oldPageI18nCodeList = this.lcdpPageI18nCodeService
         .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistory.getId()).filter(MatchPattern.EQ), new Order[0]);
      oldPageI18nCodeList.forEach(code -> {
         if (pageDependentI18n.contains(code.getCode())) {
            pageDependentI18n.remove(code.getCode());
         } else {
            deletePageI18nCodeIdList.add(code.getId());
         }
      });
      pageDependentI18n.forEach(code -> {
         LcdpPageI18nCodeBean lcdpPageI18nCodeBean = new LcdpPageI18nCodeBean();
         lcdpPageI18nCodeBean.setId(ApplicationContextHelper.getNextIdentity());
         lcdpPageI18nCodeBean.setCode(code);
         lcdpPageI18nCodeBean.setModulePageHistoryId(resourceHistory.getId());
         lcdpPageI18nCodeBean.setModulePageId(resourceHistory.getResourceId());
         newPageI18nCodeList.add(lcdpPageI18nCodeBean);
      });
      this.lcdpPageI18nCodeService.getDao().insert(newPageI18nCodeList);
      this.lcdpPageI18nCodeService.getDao().deleteByIdList(deletePageI18nCodeIdList);
   }

   private void dealResourceName(List<LcdpResourceBean> resourceList) {
      List<LcdpResourceHistoryBean> filter = resourceList.stream()
         .filter(f -> f.getId() != null && LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(f.getResourceCategory()))
         .map(e -> {
            LcdpResourceHistoryBean condition = new LcdpResourceHistoryBean();
            condition.setResourceId(e.getId());
            return condition;
         })
         .collect(Collectors.toList());
      List<LcdpResourceHistoryBean> historyList = this.lcdpResourceHistoryService
         .getDao()
         .selectList(filter, ArrayUtils.asList("RESOURCEID"), ArrayUtils.asList("ID", "RESOURCEID", "RESOURCENAME", "SUBMITFLAG", "CREATEDBYID"));
      List<LcdpResourceHistoryBean> resourceHistoryList = historyList.stream()
         .filter(f -> "no".equals(f.getSubmitFlag()) && LocalContextHelper.getLoginUserId().equals(f.getCreatedById()))
         .collect(Collectors.toList());

      for (LcdpResourceBean resource : resourceList) {
         LcdpResourceHistoryBean resourceHistory = resourceHistoryList.stream()
            .filter(f -> ObjectUtils.equals(resource.getId(), f.getResourceId()))
            .findFirst()
            .orElse(null);
         if (resourceHistory != null) {
            resource.setResourceName(resourceHistory.getResourceName());
         }
      }
   }

   private List<LcdpResourceBean> dealTreeMapper(List<LcdpResourceBean> childList) {
      return LcdpScriptUtils.validateCurrentDBMybatisMapper()
         ? childList.stream()
            .filter(
               e -> !StringUtils.equals("mapper", e.getResourceCategory())
                  || StringUtils.equals("mapper", e.getResourceCategory())
                     && StringUtils.containsIgnoreCase(e.getResourceName(), this.current_environment_database)
            )
            .collect(Collectors.toList())
         : childList;
   }

   private String buildPageConfig(String path) {
      StringBuilder sb = new StringBuilder();
      CorePageConfigDTO pageConfig = this.corePageConfigService.selectCurrentPageConfig(path);
      if (!pageConfig.getGrid().isEmpty()) {
         sb.append("Gikam.setCompParam('").append(JSON.toJSONString(pageConfig)).append("');\n");
      }

      if (LocalContextHelper.isUserLogin()) {
         List<CoreStudioAdvSearchConfigBean> searchConfigList = this.coreStudioAdvSearchConfigService.getCurrentUserConfigListByUrl(path);
         if (!CollectionUtils.isEmpty(searchConfigList)) {
            sb.append("Gikam.component.Grid.setAdvSearchConfig([");
            int i = 0;

            for (int j = searchConfigList.size(); i < j; i++) {
               CoreStudioAdvSearchConfigBean searchConfig = searchConfigList.get(i);
               if (i > 0) {
                  sb.append(",");
               }

               sb.append("{blockId:'")
                  .append(searchConfig.getBlockId())
                  .append("',url:'")
                  .append(searchConfig.getUrl())
                  .append("',json:'")
                  .append(searchConfig.getJson())
                  .append("'}");
            }

            sb.append("]);\n");
         }
      }

      return sb.toString();
   }

   private static class JavaSourceConfig {
      private Long resourceId;
      private String className;
      private String hintName;
      private String sourceCode;
      private String path;
      private Long version;
      private LcdpResourceBean resource;

      public JavaSourceConfig(String className, String hintName, String sourceCode, LcdpResourceBean resource) {
         this.resourceId = resource.getId();
         this.className = className;
         this.hintName = hintName;
         this.sourceCode = sourceCode;
         this.path = resource.getPath();
         this.version = resource.getEffectVersion();
         this.resource = resource;
      }

      public Long getResourceId() {
         return this.resourceId;
      }

      public String getPath() {
         return this.path;
      }

      public Long getVersion() {
         return this.version;
      }

      public String getClassName() {
         return this.className;
      }

      public String getHintName() {
         return this.hintName;
      }

      public String getSourceCode() {
         return this.sourceCode;
      }

      public LcdpResourceBean getResource() {
         return this.resource;
      }
   }
}
