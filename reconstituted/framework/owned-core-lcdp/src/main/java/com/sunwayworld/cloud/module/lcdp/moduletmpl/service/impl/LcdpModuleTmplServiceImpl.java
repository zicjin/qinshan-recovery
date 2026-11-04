package com.sunwayworld.cloud.module.lcdp.moduletmpl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpCustomTmplPageCompBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpCustomTmplResourceBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplChildTableDTO;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplConfigBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplDTO;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplPageCompBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplResourceBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpModuleTmplDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpCustomTmplPageCompService;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpCustomTmplResourceService;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpModuleTmplConfigService;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpModuleTmplPageCompService;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpModuleTmplResourceService;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpModuleTmplService;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.support.LcdpModuleTempHelper;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpPageI18nCodeBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageCompService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpPageI18nCodeService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceHistoryService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpMapperUtils;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpScriptUtils;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.bean.LcdpResourceDocumentBean;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.support.LuceneIndexManager;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.cloud.module.lcdp.support.LcdpScriptTemplateHelper;
import com.sunwayworld.cloud.module.lcdp.support.constant.LcdpConstant;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldDTO;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Repository
@GikamBean
public class LcdpModuleTmplServiceImpl implements LcdpModuleTmplService {
   @Autowired
   private LcdpModuleTmplDao lcdpModuleTmplDao;
   @Autowired
   private LcdpModuleTmplResourceService lcdpModuleTmplResourceService;
   @Autowired
   private LcdpModuleTmplPageCompService lcdpModuleTmplPageCompService;
   @Autowired
   private LcdpModuleTmplConfigService lcdpModuleTmplConfigService;
   @Autowired
   @Lazy
   private LcdpResourceService resourceService;
   @Autowired
   private LcdpTableService lcdpTableService;
   @Autowired
   private LcdpResourceHistoryService resourceHistoryService;
   @Autowired
   private LcdpModulePageCompService modulePageCompService;
   @Autowired
   private LcdpResourceLockService resourceLockService;
   @Autowired
   private LcdpCustomTmplResourceService customTmplResourceService;
   @Autowired
   private LcdpCustomTmplPageCompService customTmplPageCompService;
   @Autowired
   private LcdpResourceCheckoutRecordService resourceCheckoutRecordService;
   @Autowired
   private LcdpModulePageCompService lcdpModulePageCompService;
   @Autowired
   private LcdpPageI18nCodeService pageI18nCodeService;
   public static List<String> compList = ArrayUtils.asList("TabPanel", "GridToolbar", "Grid", "Button");
   private String current_environment_database = LcdpConstant.PROFILE_DB_LIST
      .stream()
      .filter(profile -> ApplicationContextHelper.isProfileActivated(profile))
      .findFirst()
      .get();

   public LcdpModuleTmplDao getDao() {
      return this.lcdpModuleTmplDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long generateCodeSourceMoudleTmpl(RestJsonWrapperBean wrapper) {
      LcdpModuleTmplDTO moduleTmplDTO = wrapper.parseUnique(LcdpModuleTmplDTO.class);
      LcdpResourceBean module = new LcdpResourceBean();
      module.setParentId(moduleTmplDTO.getParentId());
      module.setResourceDesc(moduleTmplDTO.getResourceDesc());
      module.setResourceName(moduleTmplDTO.getResourceName());
      module.setResourceCategory(moduleTmplDTO.getResourceCategory());
      module.setId(ApplicationContextHelper.getNextIdentity());
      this.resourceService.getDao().insert(module);
      if ("sys".equals(moduleTmplDTO.getTmplClassId())) {
         this.generateCodeSourceSysModuleTmpl(moduleTmplDTO, module);
      } else {
         this.generateCodeSourceCustomModuleTmpl(moduleTmplDTO, module);
      }

      return module.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      List<LcdpModuleTmplBean> deleteList = wrapper.parse(this.getDao().getType());
      if (!deleteList.isEmpty()) {
         List<Long> idList = deleteList.stream().map(LcdpModuleTmplBean::getId).collect(Collectors.toList());
         List<LcdpModuleTmplBean> moduleTmplList = this.getDao()
            .selectListByIds(idList, new Order[0])
            .stream()
            .filter(tmpl -> "sys".equals(tmpl.getTmplClassId()))
            .collect(Collectors.toList());
         if (!moduleTmplList.isEmpty()) {
            ServletUtils.responseError(HttpStatus.BAD_REQUEST.value(), I18nHelper.getMessage("LCDP.MODULETEMP.DELETE.VALIDATE.SYS_NOT_ALLOWED_DELETE"));
         } else {
            List<LcdpCustomTmplResourceBean> customTmplResourceList = this.customTmplResourceService
               .selectListByFilter(SearchFilter.instance().match("moduleTmplId", idList).filter(MatchPattern.OR), new Order[0]);
            this.getDao().deleteByIdList(idList);
            if (!customTmplResourceList.isEmpty()) {
               List<Long> customTmplResourceIdList = customTmplResourceList.stream().map(LcdpCustomTmplResourceBean::getId).collect(Collectors.toList());
               List<LcdpCustomTmplPageCompBean> customTmplPageCompList = this.customTmplPageCompService
                  .selectListByFilter(SearchFilter.instance().match("customTmplPageId", customTmplResourceIdList).filter(MatchPattern.OR), new Order[0]);
               this.customTmplResourceService.getDao().deleteByIdList(customTmplResourceIdList);
               this.customTmplPageCompService.getDao().deleteBy(customTmplPageCompList);
            }
         }
      }
   }

   private void generateCodeSourceCustomModuleTmpl(LcdpModuleTmplDTO moduleTmplDTO, LcdpResourceBean module) {
      moduleTmplDTO.setResourceId(module.getId());
      LcdpResourceBean category = this.resourceService.selectById(module.getParentId());
      Long moduleTmplId = moduleTmplDTO.getModuleTmplId();
      moduleTmplDTO.setModuleName(module.getResourceName());
      moduleTmplDTO.setCategoryName(category.getResourceName());
      moduleTmplDTO.setModulePath(category.getResourceName() + "." + module.getResourceName() + ".");
      List<LcdpCustomTmplResourceBean> customTmplResourceList = this.customTmplResourceService
         .selectListByFilter(SearchFilter.instance().match("MODULETMPLID", moduleTmplId).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpCustomTmplResourceBean> viewList = customTmplResourceList.stream()
         .filter(resource -> "view".equals(resource.getResourceCategory()))
         .collect(Collectors.toList());
      List<Long> viewIdList = viewList.stream().map(LcdpCustomTmplResourceBean::getId).collect(Collectors.toList());
      List<LcdpCustomTmplPageCompBean> customTmplPageCompList = this.customTmplPageCompService
         .selectListByFilter(SearchFilter.instance().match("CUSTOMTMPLPAGEID", viewIdList).filter(MatchPattern.OR), new Order[0]);
      Map<Long, List<LcdpCustomTmplPageCompBean>> tmplPageId2PageCompList = customTmplPageCompList.stream()
         .collect(Collectors.groupingBy(LcdpCustomTmplPageCompBean::getCustomTmplPageId));
      List<LcdpResourceBean> resourceList = new ArrayList<>();
      List<LcdpResourceHistoryBean> resourceHistoryList = new ArrayList<>();
      List<LcdpModulePageCompBean> modulePageCompList = new ArrayList<>();
      Map<Long, Long> id2Map = new HashMap<>();
      Map<Long, String> sourceId2PathMap = new HashMap<>();
      viewList.stream().forEach(customResource -> {
         LcdpResourceBean resource = new LcdpResourceBean();
         BeanUtils.copyProperties(customResource, resource, LcdpConstant.COPY_IGNORE_PROPERTIES);
         resource.setId(ApplicationContextHelper.getNextIdentity());
         sourceId2PathMap.put(resource.getId(), resource.getPath());
         id2Map.put(customResource.getId(), resource.getId());
         resource.setParentId(module.getId());
         resource.setResourceName(resource.getResourceName().replaceFirst(customResource.getModuleName(), module.getResourceName()));
         resource.setPath(moduleTmplDTO.getModulePath() + "page." + resource.getResourceName());
         resource.setClassName(null);
         resource.setEffectVersion(null);
         EntityHelper.assignCreatedElement(resource);
         resourceList.add(resource);
         LcdpResourceHistoryBean resourceHistory = new LcdpResourceHistoryBean();
         BeanUtils.copyProperties(resource, resourceHistory);
         resourceHistory.setResourceId(resource.getId());
         resourceHistory.setSubmitFlag("no");
         resourceHistory.setEffectFlag("no");
         resourceHistory.setVersion(1L);
         resourceHistory.setModifyVersion(1);
         EntityHelper.assignCreatedElement(resource);
         resourceHistoryList.add(resourceHistory);
         List<LcdpCustomTmplPageCompBean> sourceCustomTmplPageCompList = tmplPageId2PageCompList.get(customResource.getId());
         if (!sourceCustomTmplPageCompList.isEmpty()) {
            List<LcdpModulePageCompBean> originalModulePageCompList = sourceCustomTmplPageCompList.stream().map(source -> {
               LcdpModulePageCompBean modulePageComp = new LcdpModulePageCompBean();
               BeanUtils.copyProperties(source, modulePageComp);
               modulePageComp.setModulePageId(resource.getId());
               modulePageComp.setModulePageHistoryId(resourceHistory.getId());
               return modulePageComp;
            }).collect(Collectors.toList());
            List<LcdpModulePageCompBean> newModulePageCompList = this.resourceService.generatePageComps(originalModulePageCompList);
            modulePageCompList.addAll(newModulePageCompList);
         }
      });
      List<String> tableNameList = moduleTmplDTO.getTableNameList();
      tableNameList.forEach(
         tableName -> {
            LcdpResourceBean javaResource = new LcdpResourceBean();
            javaResource.setId(ApplicationContextHelper.getNextIdentity());
            javaResource.setResourceName("Lcdp" + this.firstUppercase(tableName.replaceFirst("T_", "").toLowerCase())[1] + "Service");
            javaResource.setResourceCategory("java");
            javaResource.setParentId(module.getId());
            javaResource.setPath(moduleTmplDTO.getModulePath() + "server." + javaResource.getResourceName());
            resourceList.add(javaResource);
            LcdpConstant.MAPPER_TMPL_NAME_LIST.forEach(mapperName -> {
               LcdpResourceBean mapperResource = new LcdpResourceBean();
               mapperResource.setId(ApplicationContextHelper.getNextIdentity());
               mapperResource.setResourceName("Lcdp" + this.firstUppercase(tableName.replaceFirst("T_", "").toLowerCase())[1] + mapperName);
               mapperResource.setResourceCategory("mapper");
               mapperResource.setParentId(module.getId());
               mapperResource.setPath(moduleTmplDTO.getModulePath() + "mapper." + mapperResource.getResourceName());
               resourceList.add(mapperResource);
               LcdpResourceHistoryBean mapperHistory = new LcdpResourceHistoryBean();
               mapperHistory.setId(ApplicationContextHelper.getNextIdentity());
               mapperHistory.setResourceName(mapperResource.getResourceName());
               mapperHistory.setResourceCategory("mapper");
               mapperHistory.setPath(mapperResource.getPath());
               mapperHistory.setResourceId(mapperResource.getId());
               mapperHistory.setVersion(1L);
               mapperHistory.setSubmitFlag("no");
               mapperHistory.setEffectFlag("no");
               mapperHistory.setModifyVersion(null);
               mapperHistory.setContent(LcdpScriptTemplateHelper.generateMapper(mapperResource.getPath(), tableName, mapperName));
               resourceHistoryList.add(mapperHistory);
               LcdpMapperUtils.loadMapper(mapperHistory.getPath(), false, mapperHistory.getContent());
               LcdpMapperUtils.syncMapper(mapperHistory.getPath(), false, mapperHistory.getContent());
            });
            LcdpResourceHistoryBean javaHistory = new LcdpResourceHistoryBean();
            BeanUtils.copyProperties(javaResource, javaHistory);
            javaHistory.setId(ApplicationContextHelper.getNextIdentity());
            javaHistory.setResourceId(javaResource.getId());
            javaHistory.setVersion(1L);
            javaHistory.setSubmitFlag("no");
            javaHistory.setEffectFlag("no");
            String content = LcdpScriptTemplateHelper.generateJavaScript(
               javaHistory.getResourceName(),
               tableName,
               StringUtils.replaceLast(javaHistory.getPath().replace("server.", "mapper."), "Service", "Mapper"),
               null
            );
            javaHistory.setContent(content);
            String classContent = content.replaceFirst(
               "public\\s+class\\s+" + javaHistory.getResourceName(), "public class " + module.getResourceName() + javaHistory.getResourceName() + "v1m1"
            );
            javaHistory.setClassContent(classContent);
            resourceHistoryList.add(javaHistory);
         }
      );
      this.dataToDB(resourceList, resourceHistoryList, modulePageCompList);
   }

   private void dataToDB(
      List<LcdpResourceBean> resourceList, List<LcdpResourceHistoryBean> resourceHistoryList, List<LcdpModulePageCompBean> modulePageCompList
   ) {
      this.resourceService.getDao().insert(resourceList);
      this.resourceHistoryService.getDao().insert(resourceHistoryList);
      this.modulePageCompService.getDao().insert(modulePageCompList);
      this.resourceLockService.lockResource(resourceList);
      List<LcdpResourceHistoryBean> scriptList = resourceHistoryList.stream()
         .filter(resource -> "java".equals(resource.getResourceCategory()))
         .collect(Collectors.toList());
      this.resourceService.insertServerScriptMethod(scriptList);
      List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
      resourceList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
      resourceHistoryList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
      this.addCompDocument(resourceList, resourceHistoryList, documentList);
      LuceneIndexManager.addIndex(documentList);
   }

   private void generateCodeSourceSysModuleTmpl(LcdpModuleTmplDTO moduleTmplDTO, LcdpResourceBean module) {
      moduleTmplDTO.setResourceId(module.getId());
      LcdpResourceBean category = this.resourceService.selectById(module.getParentId());
      Long moduleTmplId = moduleTmplDTO.getModuleTmplId();
      moduleTmplDTO.setModuleName(module.getResourceName());
      moduleTmplDTO.setCategoryName(category.getResourceName());
      moduleTmplDTO.setModulePath(category.getResourceName() + "." + module.getResourceName() + ".");
      moduleTmplDTO.setMasterScriptPath(
         category.getResourceName()
            + "."
            + module.getResourceName()
            + ".server.Lcdp"
            + this.firstUppercase(moduleTmplDTO.getMasterTableName().replaceFirst("T_", "").toLowerCase())[1]
            + "Service"
      );
      String bpFlag = moduleTmplDTO.getBpFlag();
      String uploaderFlag = moduleTmplDTO.getUploaderFlag();
      List<LcdpModuleTmplChildTableDTO> childTableList = moduleTmplDTO.getChildTableList();
      List<String> childTableNameList = childTableList.stream().map(LcdpModuleTmplChildTableDTO::getTableName).collect(Collectors.toList());
      List<LcdpTableDTO> childTableDTOList = this.lcdpTableService.selectPhysicalTableInfoList(childTableNameList);
      List<LcdpModuleTmplConfigBean> moduleTmplConfigList = this.lcdpModuleTmplConfigService
         .selectListByFilter(
            SearchFilter.instance()
               .match("MODULETMPLID", moduleTmplId)
               .filter(MatchPattern.EQ)
               .match("BPFLAG", bpFlag)
               .filter(MatchPattern.EQ)
               .match("UPLOADERFLAG", uploaderFlag)
               .filter(MatchPattern.EQ),
            new Order[0]
         );
      LcdpModuleTmplConfigBean moduleTmplConfig = moduleTmplConfigList.get(0);
      SearchFilter filter = SearchFilter.instance();
      List<Long> moduleTmplConfigIdList = new ArrayList<>();
      moduleTmplConfigIdList.add(moduleTmplConfig.getId());
      if ("1".equals(moduleTmplDTO.getChooseFlag())) {
         moduleTmplConfigIdList.add(LcdpConstant.MODULE_TMPL_PUBLIC_CHOOSE_PAGE_ID);
      }

      filter.match("MODULETMPLCONFIGID", moduleTmplConfigIdList).filter(MatchPattern.OR);
      List<LcdpModuleTmplResourceBean> moduleTmplResourceList = this.lcdpModuleTmplResourceService.selectListByFilter(filter, new Order[0]);
      List<LcdpModuleTmplPageCompBean> moduleTmplPageCompList = this.generatePageComp(moduleTmplResourceList, moduleTmplDTO, childTableDTOList);
      this.createResource(moduleTmplDTO, moduleTmplPageCompList);
   }

   @Override
   public void saveCustomTemplate(RestJsonWrapperBean wrapper) {
      Map<String, String> paramMap = wrapper.getParamMap();
      String resourceIdStr = paramMap.get("resourceId");
      String customTmplName = paramMap.get("customTmplName");
      LcdpModuleTmplBean moduleTmpl = new LcdpModuleTmplBean();
      moduleTmpl.setId(ApplicationContextHelper.getNextIdentity());
      moduleTmpl.setModuleTmplName(customTmplName);
      moduleTmpl.setTmplClassId("biz");
      moduleTmpl.setTmplType("module");
      this.getDao().insert(moduleTmpl);
      this.saveCustomTmplData(resourceIdStr, moduleTmpl.getId());
   }

   private void saveCustomTmplData(String resourceIdStr, Long tmplId) {
      Long resourceId = Long.valueOf(resourceIdStr);
      LcdpResourceBean module = this.resourceService.selectById(resourceId);
      List<LcdpResourceBean> childResourceList = this.resourceService
         .selectListByFilter(
            SearchFilter.instance()
               .match("PARENTID", module.getId())
               .filter(MatchPattern.EQ)
               .match("RESOURCECATEGORY", "view")
               .filter(MatchPattern.EQ)
               .match("DELETEFLAG", "0")
               .filter(MatchPattern.EQ),
            new Order[0]
         );
      if (!childResourceList.isEmpty()) {
         childResourceList = childResourceList.stream().filter(r -> !this.noReturnDataFilter(r)).collect(Collectors.toList());
         List<Long> childResourceIdList = childResourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceHistoryBean> resourceHistoryDataList = new ArrayList<>();
         List<LcdpResourceHistoryBean> resourceHistoryList = this.resourceHistoryService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", childResourceIdList).filter(MatchPattern.OR), new Order[0]);
         List<LcdpResourceHistoryBean> effectResourceList = resourceHistoryList.stream()
            .filter(resource -> "yes".equals(resource.getEffectFlag()))
            .collect(Collectors.toList());
         List<LcdpResourceHistoryBean> unSubmitResourceList = resourceHistoryList.stream()
            .filter(resource -> "no".equals(resource.getSubmitFlag()) && LocalContextHelper.getLoginUserId().equals(resource.getCreatedById()))
            .collect(Collectors.toList());
         if (!unSubmitResourceList.isEmpty()) {
            resourceHistoryDataList.addAll(unSubmitResourceList);
         }

         List<Long> editIdList = unSubmitResourceList.stream().map(LcdpResourceHistoryBean::getResourceId).collect(Collectors.toList());

         for (int i = 0; i < effectResourceList.size(); i++) {
            LcdpResourceHistoryBean effectData = effectResourceList.get(i);
            if (!editIdList.contains(effectData.getResourceId())) {
               resourceHistoryDataList.add(effectData);
            }
         }

         List<LcdpResourceHistoryBean> viewHistory = resourceHistoryDataList.stream()
            .filter(history -> "view".equals(history.getResourceCategory()))
            .collect(Collectors.toList());
         List<Long> viewIdList = viewHistory.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
         List<LcdpModulePageCompBean> modulePageCompList = this.modulePageCompService
            .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", viewIdList).filter(MatchPattern.OR), new Order[0]);
         Map<Long, List<LcdpModulePageCompBean>> historyResourceId2PageCompListMap = modulePageCompList.stream()
            .collect(Collectors.groupingBy(LcdpModulePageCompBean::getModulePageHistoryId));
         Map<Long, List<LcdpModulePageCompBean>> historyId2NewPageCompListMap = new HashMap<>();
         historyResourceId2PageCompListMap.forEach(
            (historyId, pageCompList) -> historyId2NewPageCompListMap.put(
               historyId, this.resourceService.generatePageComps((List<LcdpModulePageCompBean>)pageCompList)
            )
         );
         module.setId(ApplicationContextHelper.getNextIdentity());
         Map<Long, Long> id2Map = new HashMap<>();

         for (LcdpResourceHistoryBean resource : resourceHistoryDataList) {
            Long id = ApplicationContextHelper.getNextIdentity();
            id2Map.put(resource.getId(), id);
            resource.setId(id);
         }

         List<LcdpModulePageCompBean> modulePageCompDataList = new ArrayList<>();
         historyId2NewPageCompListMap.forEach((k, v) -> {
            for (LcdpModulePageCompBean pageComp : v) {
               pageComp.setModulePageHistoryId(id2Map.get(k));
            }

            modulePageCompDataList.addAll(v);
         });
         LcdpCustomTmplResourceBean customTmplResource = new LcdpCustomTmplResourceBean();
         BeanUtils.copyProperties(module, customTmplResource);
         customTmplResource.setModuleTmplId(tmplId);
         List<LcdpCustomTmplResourceBean> customTmplResourceList = resourceHistoryDataList.stream().map(resourceHistory -> {
            LcdpCustomTmplResourceBean childCustomTmplResource = new LcdpCustomTmplResourceBean();
            BeanUtils.copyProperties(resourceHistory, childCustomTmplResource);
            childCustomTmplResource.setModuleTmplId(tmplId);
            childCustomTmplResource.setParentId(module.getId());
            childCustomTmplResource.setModuleName(module.getResourceName());
            return childCustomTmplResource;
         }).collect(Collectors.toList());
         List<LcdpCustomTmplPageCompBean> customTmplPageCompList = modulePageCompDataList.stream().map(modulePageComp -> {
            LcdpCustomTmplPageCompBean customTmplPageComp = new LcdpCustomTmplPageCompBean();
            BeanUtils.copyProperties(modulePageComp, customTmplPageComp);
            customTmplPageComp.setCustomTmplPageId(modulePageComp.getModulePageHistoryId());
            return customTmplPageComp;
         }).collect(Collectors.toList());
         this.customTmplResourceService.getDao().insert(customTmplResourceList);
         this.customTmplPageCompService.getDao().insert(customTmplPageCompList);
      }
   }

   private void createResource(LcdpModuleTmplDTO moduleTmplDTO, List<LcdpModuleTmplPageCompBean> moduleTmplPageCompList) {
      List<LcdpResourceBean> resourceList = new ArrayList<>();
      List<LcdpResourceHistoryBean> javaHistoryList = new ArrayList<>();
      List<LcdpResourceHistoryBean> resourceHistoryList = new ArrayList<>();
      List<LcdpModulePageCompBean> modulePageCompList = new ArrayList<>();
      List<LcdpPageI18nCodeBean> pageI18nCodeList = new ArrayList<>();
      Map<String, List<LcdpModuleTmplPageCompBean>> pageTypeMap = moduleTmplPageCompList.stream()
         .collect(Collectors.groupingBy(LcdpModuleTmplPageCompBean::getPageType));
      pageTypeMap.forEach(
         (pageType, compList) -> {
            LcdpResourceBean page = new LcdpResourceBean();
            page.setId(ApplicationContextHelper.getNextIdentity());
            page.setParentId(moduleTmplDTO.getResourceId());
            page.setResourceName(moduleTmplDTO.getResourceName() + pageType);
            page.setResourceDesc(moduleTmplDTO.getResourceDesc());
            page.setResourceCategory("view");
            page.setPath(moduleTmplDTO.getModulePath() + "page." + page.getResourceName());
            EntityHelper.assignCreatedElement(page);
            resourceList.add(page);
            LcdpResourceHistoryBean pageHistory = new LcdpResourceHistoryBean();
            BeanUtils.copyProperties(page, pageHistory);
            pageHistory.setId(ApplicationContextHelper.getNextIdentity());
            pageHistory.setResourceId(page.getId());
            pageHistory.setVersion(1L);
            pageHistory.setEffectFlag("no");
            pageHistory.setSubmitFlag("no");
            pageHistory.setContent(this.generatePageContent(pageType, moduleTmplDTO));
            resourceHistoryList.add(pageHistory);
            List<LcdpModulePageCompBean> tempPageCompList = compList.stream().map(comp -> {
               LcdpModulePageCompBean pageCompx = new LcdpModulePageCompBean();
               BeanUtils.copyProperties(comp, pageCompx);
               pageCompx.setModulePageId(page.getId());
               pageCompx.setModulePageHistoryId(pageHistory.getId());
               pageCompx.setModulePageVersion(1L);
               return pageCompx;
            }).collect(Collectors.toList());
            List<LcdpModulePageCompBean> pageCompList = this.resourceService.generatePageComps(tempPageCompList);
            List<LcdpPageI18nCodeBean> pageUseI18nCodeList = new ArrayList<>();

            for (LcdpModulePageCompBean pageComp : pageCompList) {
               String config = StringUtils.isEmpty(pageComp.getConfigExtend()) ? pageComp.getConfig() : pageComp.getConfig() + pageComp.getConfigExtend();
               JSONObject configObject = JSON.parseObject(config);
               JSONObject title = configObject.getJSONObject("title");
               JSONObject text = configObject.getJSONObject("text");
               if (!ObjectUtils.isEmpty(title) && StringUtils.equals(title.getString("type"), "i18n")) {
                  LcdpPageI18nCodeBean pageI18nCode = new LcdpPageI18nCodeBean();
                  pageI18nCode.setId(ApplicationContextHelper.getNextIdentity());
                  pageI18nCode.setModulePageId(pageComp.getModulePageId());
                  pageI18nCode.setModulePageHistoryId(pageComp.getModulePageHistoryId());
                  pageI18nCode.setCode(title.getString("i18nCode"));
                  pageUseI18nCodeList.add(pageI18nCode);
               }

               if (!ObjectUtils.isEmpty(text) && StringUtils.equals(text.getString("type"), "i18n")) {
                  LcdpPageI18nCodeBean pageI18nCode = new LcdpPageI18nCodeBean();
                  pageI18nCode.setId(ApplicationContextHelper.getNextIdentity());
                  pageI18nCode.setModulePageId(pageComp.getModulePageId());
                  pageI18nCode.setModulePageHistoryId(pageComp.getModulePageHistoryId());
                  pageI18nCode.setCode(text.getString("i18nCode"));
                  pageUseI18nCodeList.add(pageI18nCode);
               }
            }

            pageUseI18nCodeList = pageUseI18nCodeList.stream()
               .collect(
                  Collectors.collectingAndThen(
                     Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LcdpPageI18nCodeBean::getCode))), ArrayList::new
                  )
               );
            pageI18nCodeList.addAll(pageUseI18nCodeList);
            modulePageCompList.addAll(pageCompList);
         }
      );
      LcdpResourceBean javaScript = new LcdpResourceBean();
      javaScript.setId(ApplicationContextHelper.getNextIdentity());
      javaScript.setParentId(moduleTmplDTO.getResourceId());
      javaScript.setResourceName("Lcdp" + this.firstUppercase(moduleTmplDTO.getMasterTableName().replaceFirst("T_", "").toLowerCase())[1] + "Service");
      javaScript.setResourceDesc(moduleTmplDTO.getResourceDesc());
      javaScript.setResourceCategory("java");
      javaScript.setPath(moduleTmplDTO.getModulePath() + "server." + javaScript.getResourceName());
      EntityHelper.assignCreatedElement(javaScript);
      LcdpResourceHistoryBean javaScriptHistory = new LcdpResourceHistoryBean();
      BeanUtils.copyProperties(javaScript, javaScriptHistory);
      javaScriptHistory.setId(ApplicationContextHelper.getNextIdentity());
      javaScriptHistory.setResourceId(javaScript.getId());
      javaScriptHistory.setVersion(1L);
      javaScriptHistory.setEffectFlag("no");
      javaScriptHistory.setSubmitFlag("no");
      String content = javaScriptHistory.getContent();
      if ("1".equals(moduleTmplDTO.getBpFlag())) {
         content = LcdpScriptTemplateHelper.generateJavaScript(
            javaScriptHistory.getResourceName(),
            moduleTmplDTO.getMasterTableName(),
            StringUtils.replaceLast(javaScriptHistory.getPath().replace("server.", "mapper."), "Service", "Mapper"),
            moduleTmplDTO.getBpFlag()
         );
      } else {
         content = LcdpScriptTemplateHelper.generateJavaScript(
            javaScriptHistory.getResourceName(),
            moduleTmplDTO.getMasterTableName(),
            StringUtils.replaceLast(javaScriptHistory.getPath().replace("server.", "mapper."), "Service", "Mapper"),
            null
         );
      }

      javaScriptHistory.setContent(content);
      String classContent = content.replaceFirst(
         "public\\s+class\\s+" + javaScriptHistory.getResourceName(),
         "public class " + moduleTmplDTO.getModuleName() + javaScriptHistory.getResourceName() + "v1m1"
      );
      javaScriptHistory.setClassContent(classContent);
      EntityHelper.assignCreatedElement(javaScript);
      resourceList.add(javaScript);
      resourceHistoryList.add(javaScriptHistory);
      javaHistoryList.add(javaScriptHistory);

      for (LcdpModuleTmplChildTableDTO childTableDTO : moduleTmplDTO.getChildTableList()) {
         LcdpResourceBean childJavaScript = new LcdpResourceBean();
         childJavaScript.setId(ApplicationContextHelper.getNextIdentity());
         childJavaScript.setParentId(moduleTmplDTO.getResourceId());
         childJavaScript.setResourceCategory("java");
         childJavaScript.setResourceName("Lcdp" + this.firstUppercase(childTableDTO.getTableName().replaceFirst("T_", "").toLowerCase())[1] + "Service");
         childJavaScript.setResourceDesc(moduleTmplDTO.getResourceDesc());
         childJavaScript.setPath(moduleTmplDTO.getModulePath() + "server." + childJavaScript.getResourceName());
         EntityHelper.assignCreatedElement(childJavaScript);
         resourceList.add(childJavaScript);
         LcdpResourceHistoryBean childJavaScriptHistory = new LcdpResourceHistoryBean();
         BeanUtils.copyProperties(childJavaScript, childJavaScriptHistory);
         childJavaScriptHistory.setId(ApplicationContextHelper.getNextIdentity());
         childJavaScriptHistory.setResourceId(childJavaScript.getId());
         childJavaScriptHistory.setVersion(1L);
         childJavaScriptHistory.setEffectFlag("no");
         childJavaScriptHistory.setSubmitFlag("no");
         String childContent = LcdpScriptTemplateHelper.generateJavaScript(
            childJavaScriptHistory.getResourceName(),
            childTableDTO.getTableName(),
            StringUtils.replaceLast(childJavaScriptHistory.getPath().replace("server.", "mapper."), "Service", "Mapper"),
            null
         );
         childJavaScriptHistory.setContent(childContent);
         javaScriptHistory.setContent(content);
         String childClassContent = childContent.replaceFirst(
            "public\\s+class\\s+" + childJavaScriptHistory.getResourceName(),
            "public class " + moduleTmplDTO.getModuleName() + childJavaScriptHistory.getResourceName() + "v1m1"
         );
         childJavaScriptHistory.setClassContent(childClassContent);
         EntityHelper.assignCreatedElement(childJavaScriptHistory);
         javaHistoryList.add(childJavaScriptHistory);
         resourceHistoryList.add(childJavaScriptHistory);
      }

      LcdpConstant.MAPPER_TMPL_NAME_LIST.forEach(mapperName -> {
         LcdpResourceBean mapper = new LcdpResourceBean();
         mapper.setId(ApplicationContextHelper.getNextIdentity());
         mapper.setResourceName("Lcdp" + this.firstUppercase(moduleTmplDTO.getMasterTableName().replaceFirst("T_", "").toLowerCase())[1] + mapperName);
         mapper.setResourceDesc(moduleTmplDTO.getResourceDesc());
         mapper.setResourceCategory("mapper");
         mapper.setParentId(moduleTmplDTO.getResourceId());
         mapper.setPath(moduleTmplDTO.getModulePath() + "mapper." + mapper.getResourceName());
         EntityHelper.assignCreatedElement(mapper);
         resourceList.add(mapper);
         LcdpResourceHistoryBean mapperHistory = new LcdpResourceHistoryBean();
         mapperHistory.setId(ApplicationContextHelper.getNextIdentity());
         mapperHistory.setResourceName(mapper.getResourceName());
         mapperHistory.setResourceCategory("mapper");
         mapperHistory.setResourceDesc(moduleTmplDTO.getResourceDesc());
         mapperHistory.setPath(mapper.getPath());
         mapperHistory.setResourceId(mapper.getId());
         mapperHistory.setVersion(1L);
         mapperHistory.setSubmitFlag("no");
         mapperHistory.setEffectFlag("no");
         mapperHistory.setModifyVersion(null);
         mapperHistory.setContent(LcdpScriptTemplateHelper.generateMapper(mapper.getPath(), moduleTmplDTO.getMasterTableName(), mapperName));
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

         for (LcdpModuleTmplChildTableDTO childTableDTO : moduleTmplDTO.getChildTableList()) {
            LcdpResourceBean childMapper = new LcdpResourceBean();
            childMapper.setId(ApplicationContextHelper.getNextIdentity());
            childMapper.setResourceName("Lcdp" + this.firstUppercase(childTableDTO.getTableName().replaceFirst("T_", "").toLowerCase())[1] + mapperName);
            childMapper.setResourceDesc(moduleTmplDTO.getResourceDesc());
            childMapper.setResourceCategory("mapper");
            childMapper.setParentId(moduleTmplDTO.getResourceId());
            childMapper.setPath(moduleTmplDTO.getModulePath() + "mapper." + childMapper.getResourceName());
            EntityHelper.assignCreatedElement(childMapper);
            resourceList.add(childMapper);
            LcdpResourceHistoryBean childMapperHistory = new LcdpResourceHistoryBean();
            BeanUtils.copyProperties(childMapper, childMapperHistory);
            childMapperHistory.setId(ApplicationContextHelper.getNextIdentity());
            childMapperHistory.setResourceId(childMapper.getId());
            childMapperHistory.setVersion(1L);
            childMapperHistory.setEffectFlag("no");
            childMapperHistory.setSubmitFlag("no");
            childMapperHistory.setContent(LcdpScriptTemplateHelper.generateMapper(childMapper.getPath(), childTableDTO.getTableName(), mapperName));
            if (LcdpScriptUtils.validateCurrentDBMybatisMapper() && !StringUtils.containsIgnoreCase(mapperName, this.current_environment_database)) {
               childMapperHistory.setSubmitFlag("yes");
               childMapperHistory.setEffectFlag("yes");
               childMapper.setContent(mapperHistory.getContent());
               childMapper.setEffectVersion(1L);
            }

            EntityHelper.assignCreatedElement(childMapperHistory);
            LcdpMapperUtils.loadMapper(childMapperHistory.getPath(), false, childMapperHistory.getContent());
            LcdpMapperUtils.syncMapper(childMapperHistory.getPath(), false, childMapperHistory.getContent());
            if (StringUtils.equals("no", childMapperHistory.getSubmitFlag())) {
               LcdpMapperUtils.loadMapper(childMapperHistory.getPath(), false, childMapperHistory.getContent());
               LcdpMapperUtils.syncMapper(childMapperHistory.getPath(), false, childMapperHistory.getContent());
            }

            resourceHistoryList.add(childMapperHistory);
         }
      });
      this.resourceService.getDao().insert(resourceList);
      this.resourceHistoryService.getDao().insert(resourceHistoryList);
      this.modulePageCompService.getDao().insert(modulePageCompList);
      this.resourceService.insertServerScriptMethod(javaHistoryList);
      List<LcdpResourceBean> afterDealMapperResourceList = resourceList.stream().filter(e -> e.getEffectVersion() == null).collect(Collectors.toList());
      this.resourceLockService.lockResource(afterDealMapperResourceList);
      this.pageI18nCodeService.getDao().insert(pageI18nCodeList);
      this.resourceCheckoutRecordService.checkoutResource(afterDealMapperResourceList);
      List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
      resourceList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
      resourceHistoryList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
      this.addCompDocument(resourceList, resourceHistoryList, documentList);
      LuceneIndexManager.addIndex(documentList);
   }

   private List<LcdpModuleTmplPageCompBean> generatePageComp(
      List<LcdpModuleTmplResourceBean> moduleTmplResourceList, LcdpModuleTmplDTO moduleTmplDTO, List<LcdpTableDTO> childTableDTOList
   ) {
      List<Long> resourceIdList = moduleTmplResourceList.stream().map(LcdpModuleTmplResourceBean::getId).collect(Collectors.toList());
      List<LcdpModuleTmplPageCompBean> moduleTmplPageCompList = this.lcdpModuleTmplPageCompService
         .selectListByFilter(SearchFilter.instance().match("MODULETMPLPAGEID", resourceIdList).filter(MatchPattern.OR), new Order[0]);
      List<LcdpModuleTmplPageCompBean> newCompList = new ArrayList<>();
      Map<Long, List<LcdpModuleTmplPageCompBean>> tmplPageId2PageCompList = moduleTmplPageCompList.stream()
         .collect(Collectors.groupingBy(LcdpModuleTmplPageCompBean::getModuleTmplPageId));
      tmplPageId2PageCompList.forEach(
         (k, pageCompList) -> this.buildPageComp(newCompList, (List<LcdpModuleTmplPageCompBean>)pageCompList, moduleTmplDTO, childTableDTOList)
      );
      moduleTmplPageCompList.addAll(newCompList);
      List<LcdpModuleTmplPageCompBean> gridToolbarList = moduleTmplPageCompList.stream()
         .filter(comp -> "GridToolbar".equals(comp.getType()))
         .collect(Collectors.toList());
      if (!gridToolbarList.isEmpty()) {
         gridToolbarList.forEach(toolbar -> {
            JSONObject toolBarConfig = JSON.parseObject(toolbar.getConfig());
            toolBarConfig.put("toolbarWrap", false);
            toolbar.setConfig(toolBarConfig.toJSONString());
         });
      }

      List<LcdpModuleTmplPageCompBean> buttonList = moduleTmplPageCompList.stream()
         .filter(comp -> "Button".equals(comp.getType()))
         .collect(Collectors.toList());
      if (!buttonList.isEmpty()) {
         for (int i = 0; i < buttonList.size(); i++) {
            JSONObject buttonConfig = JSON.parseObject(buttonList.get(i).getConfig());
            if (buttonConfig.getString("id").startsWith("Button_")) {
               buttonConfig.put("id", "Button_" + (i + 1));
               if (StringUtils.equals(buttonConfig.getString("iconType"), "submit")) {
                  buttonConfig.put("iconType", "batch-submit");
               }
            }

            if (buttonConfig.getString("id").startsWith("detail-button-submit") && StringUtils.equals(buttonConfig.getString("iconType"), "submit")) {
               buttonConfig.put("iconType", "batch-submit");
            }

            this.dealButtonI18NCode(buttonConfig);
            buttonList.get(i).setConfig(buttonConfig.toJSONString());
         }
      }

      List<LcdpModuleTmplPageCompBean> tabPanelList = moduleTmplPageCompList.stream()
         .filter(comp -> "TabPanel".equals(comp.getType()))
         .collect(Collectors.toList());
      List<String> idIndexList = tabPanelList.stream()
         .map(
            tabPanelx -> {
               String tabPanelConfigStr = StringUtils.isEmpty(tabPanelx.getConfigExtend())
                  ? tabPanelx.getConfig()
                  : tabPanelx.getConfig() + tabPanelx.getConfigExtend();
               JSONObject panelConfigx = JSON.parseObject(tabPanelConfigStr);
               return panelConfigx.getString("id").split("_")[1];
            }
         )
         .filter(indexx -> !StringUtils.isEmpty(indexx) && NumberUtils.isNumber(indexx))
         .collect(Collectors.toList());
      Long index = 0L;
      if (!idIndexList.isEmpty()) {
         index = idIndexList.stream().map(Long::valueOf).max(Comparator.comparing(Function.identity())).get();
      }

      for (int i = 0; i < tabPanelList.size(); i++) {
         LcdpModuleTmplPageCompBean tabPanel = tabPanelList.get(i);
         JSONObject panelConfig = JSON.parseObject(tabPanel.getConfig());
         if (panelConfig.getString("id").startsWith("TabPanel_")) {
            panelConfig.put("id", "TabPanel_" + (index + (i + 1)));
         }

         tabPanelList.get(i).setConfig(panelConfig.toJSONString());
      }

      return moduleTmplPageCompList;
   }

   private void dealButtonI18NCode(JSONObject buttonConfig) {
      String method = buttonConfig.getString("onClick__$S$__");
      String buttonI118NCode = LcdpConstant.BUTTON_I18N_MAP.get(method);
      if (method.startsWith("insert")) {
         buttonI118NCode = LcdpConstant.BUTTON_I18N_MAP.get("insert");
      }

      if (method.startsWith("delete")) {
         buttonI118NCode = LcdpConstant.BUTTON_I18N_MAP.get("delete");
      }

      JSONObject text = buttonConfig.getJSONObject("text");
      text.put("type", "i18n");
      text.put("i18nCode", buttonI118NCode);
      text.remove("en-US");
      buttonConfig.put("text", text);
   }

   private void buildPageComp(
      List<LcdpModuleTmplPageCompBean> newCompList,
      List<LcdpModuleTmplPageCompBean> pageCompList,
      LcdpModuleTmplDTO moduleTmplDTO,
      List<LcdpTableDTO> childTableDTOList
   ) {
      LcdpModuleTmplDTO dealTmplDTO = new LcdpModuleTmplDTO();
      BeanUtils.copyProperties(moduleTmplDTO, dealTmplDTO);
      String masterTableName = dealTmplDTO.getMasterTableName();
      LcdpTableDTO masterTableDTO = this.lcdpTableService.selectPhysicalTableInfo(masterTableName);
      List<LcdpTableFieldDTO> masterFieldList = masterTableDTO.getFieldList();
      masterFieldList = masterFieldList.stream().skip(1L).limit(50L).collect(Collectors.toList());
      LcdpModuleTmplPageCompBean masterDataGrid = pageCompList.stream()
         .filter(comp -> "Grid".equals(comp.getType()) && "master".equals(comp.getCompTag()))
         .findFirst()
         .orElse(null);
      if (!ObjectUtils.isEmpty(masterDataGrid)) {
         this.dealMasterGrid(dealTmplDTO, newCompList, masterFieldList, masterDataGrid);
      }

      LcdpModuleTmplPageCompBean formComp = pageCompList.stream()
         .filter(comp -> "Form".equals(comp.getType()) && "master".equals(comp.getCompTag()))
         .findFirst()
         .orElse(null);
      if (!ObjectUtils.isEmpty(formComp)) {
         this.dealMasterForm(dealTmplDTO, newCompList, masterFieldList, formComp);
      }

      List<LcdpModuleTmplPageCompBean> tabList = pageCompList.stream().filter(comp -> "Tab".equals(comp.getType())).collect(Collectors.toList());
      LcdpModuleTmplPageCompBean tab = new LcdpModuleTmplPageCompBean();
      List<LcdpModuleTmplPageCompBean> childTabList = tabList.stream().filter(comp -> "child".equals(comp.getCompTag())).collect(Collectors.toList());
      if (childTabList.isEmpty() && !tabList.isEmpty()) {
         tab = tabList.get(0);
      } else if (!childTabList.isEmpty()) {
         tab = childTabList.get(0);
      }

      JSONObject tabConfig = new JSONObject();
      JSONArray tabChildrenWidgetId = new JSONArray();
      if (!ObjectUtils.isEmpty(tab.getConfig())) {
         tabConfig = JSON.parseObject(tab.getConfig());
         tabChildrenWidgetId = (JSONArray)tabConfig.get("childrenWidgetId");
      }

      if (!childTableDTOList.isEmpty()) {
         Map<String, LcdpTableDTO> tableName2TableDTOMap = childTableDTOList.stream()
            .collect(Collectors.toMap(LcdpTableDTO::getTableName, Function.identity()));
         List<LcdpModuleTmplPageCompBean> gridCompList = pageCompList.stream()
            .filter(comp -> compList.contains(comp.getType()) && "child".equals(comp.getCompTag()))
            .collect(Collectors.toList());
         if (!gridCompList.isEmpty()) {
            tableName2TableDTOMap.forEach(
               (table, tableDTO) -> {
                  List<LcdpTableFieldDTO> fieldDTOList = tableDTO.getFieldList()
                     .stream()
                     .filter(field -> !StringUtils.equals("ID", field.getFieldName()))
                     .collect(Collectors.toList());
                  tableDTO.setFieldList(fieldDTOList);
                  this.copyChildPanelAndGrid(dealTmplDTO, gridCompList, tableDTO, newCompList);
               }
            );
         }

         String tabId = tab.getId();
         gridCompList.stream().filter(comp -> "TabPanel".equals(comp.getType())).forEach(comp -> comp.setParentId("remove"));
         List<LcdpModuleTmplPageCompBean> newTabPanelList = newCompList.stream()
            .filter(comp -> "TabPanel".equals(comp.getType()) && null == comp.getCompTag() && comp.getParentId().equals(tabId))
            .collect(Collectors.toList());
         List<String> newTabPanelIdList = newTabPanelList.stream().map(LcdpModuleTmplPageCompBean::getId).collect(Collectors.toList());
         if (!newTabPanelIdList.isEmpty()) {
            tabChildrenWidgetId.remove("child".equals(tab.getCompTag()) ? 0 : 1);
            Object uploaderId = null;
            int dealIndex = "child".equals(tab.getCompTag()) ? 0 : 1;
            if (tabChildrenWidgetId.size() > dealIndex) {
               uploaderId = tabChildrenWidgetId.get(tabChildrenWidgetId.size() - 1);
               tabChildrenWidgetId.remove(tabChildrenWidgetId.size() - 1);
            }

            for (String id : newTabPanelIdList) {
               tabChildrenWidgetId.add(id);
            }

            if (null != uploaderId) {
               tabChildrenWidgetId.add(uploaderId);
            }
         }
      } else {
         pageCompList.stream()
            .filter(comp -> "child".equals(comp.getCompTag()) && "TabPanel".equals(comp.getType()))
            .forEach(comp -> comp.setParentId("remove"));
         if (tabChildrenWidgetId.size() > 0) {
            if (moduleTmplDTO.getModuleTmplId() != LcdpConstant.MODULE_TMPL_UP_DOWN_ID) {
               tabChildrenWidgetId.remove("child".equals(tab.getCompTag()) ? 0 : 1);
            } else {
               tabChildrenWidgetId.remove(0);
            }
         }
      }

      if (tabChildrenWidgetId.size() == 0) {
         tab.setParentId("remove");
         String tabId = tab.getId();
         pageCompList.stream().filter(comp -> "LayoutCenter".equals(comp.getType()) && !StringUtils.equals("master", comp.getCompTag())).forEach(comp -> {
            int removeId = 0;
            JSONObject layoutCenterConfig = JSON.parseObject(comp.getConfig());
            JSONArray childrenWidgetId = (JSONArray)layoutCenterConfig.get("childrenWidgetId");

            for (int i = 0; i < childrenWidgetId.size(); i++) {
               if (childrenWidgetId.getString(i).equals(tabId)) {
                  removeId = i;
               }
            }

            childrenWidgetId.remove(removeId);
            layoutCenterConfig.put("childrenWidgetId", childrenWidgetId);
            comp.setConfig(layoutCenterConfig.toJSONString());
         });
      } else {
         tabConfig.put("childrenWidgetId", tabChildrenWidgetId);
         tab.setConfig(tabConfig.toJSONString());
      }

      List<LcdpModuleTmplPageCompBean> uploaderList = pageCompList.stream().filter(comp -> "Uploader".equals(comp.getType())).collect(Collectors.toList());
      if (!uploaderList.isEmpty()) {
         LcdpModuleTmplPageCompBean uploaderComp = uploaderList.get(0);
         JSONObject uploaderConfig = JSON.parseObject(uploaderComp.getConfig());
         uploaderConfig.put("id", String.format(uploaderConfig.getString("id"), dealTmplDTO.getModuleName().toLowerCase()));
         uploaderConfig.put("dbTable", dealTmplDTO.getMasterTableName());
         uploaderConfig.put("preview", true);
         uploaderConfig.put("gridPage", true);
         uploaderConfig.remove("toolbarAlign");
         uploaderConfig.put("dependentWidgetId", dealTmplDTO.getDependentWidgetId());
         uploaderComp.setConfig(uploaderConfig.toJSONString());
      }

      List<LcdpModuleTmplPageCompBean> windowToolbarList = pageCompList.stream()
         .filter(comp -> "WindowToolbar".equals(comp.getType()))
         .collect(Collectors.toList());
      if (!windowToolbarList.isEmpty()) {
         windowToolbarList.forEach(toolbar -> {
            JSONObject toolBarConfig = JSON.parseObject(toolbar.getConfig());
            toolBarConfig.remove("toolbarAlign");
            toolbar.setConfig(toolBarConfig.toJSONString());
         });
      }

      List<LcdpModuleTmplPageCompBean> gridToolbarList = pageCompList.stream()
         .filter(comp -> "GridToolbar".equals(comp.getType()))
         .collect(Collectors.toList());
      List<LcdpModuleTmplPageCompBean> newGridToolBarList = newCompList.stream()
         .filter(comp -> "GridToolbar".equals(comp.getType()))
         .collect(Collectors.toList());
      if (!gridToolbarList.isEmpty()) {
         if (!newGridToolBarList.isEmpty()) {
            gridToolbarList.addAll(newGridToolBarList);
         }

         gridToolbarList.forEach(toolbar -> {
            JSONObject toolBarConfig = JSON.parseObject(toolbar.getConfig());
            toolBarConfig.remove("toolbarAlign");
            toolbar.setConfig(toolBarConfig.toJSONString());
         });
      }

      List<LcdpModuleTmplPageCompBean> tabPanelList = pageCompList.stream().filter(comp -> "TabPanel".equals(comp.getType())).collect(Collectors.toList());
      tabPanelList.forEach(tabPanel -> {
         JSONObject panelConfig = JSON.parseObject(tabPanel.getConfig());
         JSONObject title = panelConfig.getJSONObject("title");
         if (StringUtils.equals(title.getString("zh-CN"), "基本信息")) {
            title.put("type", "i18n");
            title.put("i18nCode", "GIKAM.FORM.BASE_INFO");
            title.remove("en-US");
            panelConfig.put("title", title);
            tabPanel.setConfig(panelConfig.toJSONString());
         }
      });
   }

   private void copyChildPanelAndGrid(
      LcdpModuleTmplDTO moduleTmplDTO, List<LcdpModuleTmplPageCompBean> gridCompList, LcdpTableDTO tableDTO, List<LcdpModuleTmplPageCompBean> newCompList
   ) {
      LcdpModuleTmplPageCompBean tabPanelComp = gridCompList.stream().filter(comp -> "TabPanel".equals(comp.getType())).findFirst().get();
      LcdpModuleTmplPageCompBean copyTabPanel = new LcdpModuleTmplPageCompBean();
      BeanUtils.copyProperties(tabPanelComp, copyTabPanel);
      copyTabPanel.setId(StringUtils.randomUUID());
      copyTabPanel.setCompTag(null);
      JSONObject panelConfig = JSON.parseObject(copyTabPanel.getConfig());
      Map<String, Object> titleMap = panelConfig.getObject("title", Map.class);
      titleMap.put("en-US", tableDTO.getTableName());
      titleMap.put("zh-CN", tableDTO.getTableDesc());
      titleMap.put("type", "i18n");
      titleMap.put("i18nCode", tableDTO.getTableName() + ".SERVICE_NAME");
      titleMap.remove("en-US");
      panelConfig.put("title", titleMap);
      LcdpModuleTmplPageCompBean gridComp = gridCompList.stream().filter(comp -> "Grid".equals(comp.getType())).findFirst().get();
      LcdpModuleTmplPageCompBean copyGrid = new LcdpModuleTmplPageCompBean();
      BeanUtils.copyProperties(gridComp, copyGrid);
      copyGrid.setId(StringUtils.randomUUID());
      copyGrid.setParentId(copyTabPanel.getId());
      panelConfig.put("childrenWidgetId", new String[]{copyGrid.getId()});
      copyTabPanel.setConfig(panelConfig.toJSONString());
      JSONObject gridConfig = JSON.parseObject(copyGrid.getConfig());
      String gridId = String.format(
         gridConfig.getString("id"),
         moduleTmplDTO.getModuleName().toLowerCase(),
         tableDTO.getTableName().toLowerCase().replaceFirst("t_", "").replaceAll("_", "-")
      );
      List<LcdpTableFieldDTO> fieldList = tableDTO.getFieldList().stream().limit(50L).collect(Collectors.toList());
      List<LcdpTableFieldDTO> createFieldList = fieldList.stream()
         .filter(mf -> LcdpConstant.CREATE_COLUMN_LIST.contains(mf.getFieldName().toUpperCase()))
         .collect(Collectors.toList());
      fieldList.removeAll(createFieldList);
      List<LcdpTableFieldDTO> columnConfigList = this.buildGridColumnConfigList(gridId, fieldList);
      columnConfigList.addAll(this.buildGridColumnConfigList(gridId, createFieldList));
      List<LcdpModuleTmplPageCompBean> gridColumnList = new ArrayList<>();
      gridColumnList.addAll(this.bulidCommonColumns(copyGrid.getId(), copyGrid.getPageType()));
      columnConfigList.forEach(field -> {
         LcdpModuleTmplPageCompBean gridColumn = new LcdpModuleTmplPageCompBean();
         gridColumn.setId(StringUtils.randomUUID());
         gridColumn.setParentId(copyGrid.getId());
         gridColumn.setType("GridColumn");
         JSONObject columnConfig = JSON.parseObject(field.getExt$Item("columnconfig"));
         if (LcdpConstant.CREATE_COLUMN_LIST.contains(field.getFieldName().toUpperCase())) {
            columnConfig.put("editor", false);
            field.setExt$Item("columnconfig", columnConfig.toJSONString());
         }

         if (!StringUtils.equals(gridComp.getPageType(), "EditPage") && moduleTmplDTO.getModuleTmplId() != LcdpConstant.MODULE_TEPL_PENETRATION) {
            columnConfig.put("editor", false);
            field.setExt$Item("columnconfig", columnConfig.toJSONString());
         }

         gridColumn.setConfig(this.fieldToAddValidateConfig(field, "column"));
         gridColumn.setPageType(copyGrid.getPageType());
         gridColumnList.add(gridColumn);
      });
      gridColumnList.forEach(
         column -> {
            JSONObject columnConfig = JSON.parseObject(column.getConfig());
            if (!StringUtils.equals(columnConfig.getString("gridColumnType"), "checkbox")
               && !StringUtils.equals(columnConfig.getString("gridColumnType"), "index")) {
               JSONObject title = columnConfig.getJSONObject("title");
               title.put("type", "i18n");
               title.put("i18nCode", (tableDTO.getTableName() + "." + columnConfig.getString("field")).toUpperCase());
               title.remove("en-US");
               columnConfig.put("title", title);
               column.setConfig(columnConfig.toJSONString());
            }
         }
      );
      List<String> gridColumnIdList = gridColumnList.stream().map(LcdpModuleTmplPageCompBean::getId).collect(Collectors.toList());
      gridConfig.put("childrenWidgetId", gridColumnIdList.toArray());
      gridConfig.put("id", gridId);
      String urlPath = moduleTmplDTO.getModulePath()
         + "server.Lcdp"
         + this.firstUppercase(tableDTO.getTableName().replaceFirst("T_", "").toLowerCase())[1]
         + "Service";
      gridConfig.put("url", String.format(gridConfig.getString("url"), urlPath));
      gridConfig.put("getRequestData__$S$__", "get" + this.firstUppercase(tableDTO.getTableName().replaceFirst("T_", "").toLowerCase())[1] + "RequestData");
      String instantSavePath = urlPath + ".updateData";
      gridConfig.put("instantSavePath", instantSavePath);
      LcdpModuleTmplPageCompBean gridToolbarComp = gridCompList.stream().filter(comp -> "GridToolbar".equals(comp.getType())).findFirst().get();
      LcdpModuleTmplPageCompBean copyGridToolbar = new LcdpModuleTmplPageCompBean();
      BeanUtils.copyProperties(gridToolbarComp, copyGridToolbar);
      copyGridToolbar.setId(StringUtils.randomUUID());
      copyGridToolbar.setParentId(copyGrid.getId());
      gridConfig.put("toolbar_childrenWidgetId", new String[]{copyGridToolbar.getId()});
      gridConfig.put("confirmField", columnConfigList.stream().findFirst().get().getFieldName().toLowerCase());
      if (LcdpConstant.MODULE_TMPL_LEFT_RIGHT_ID == moduleTmplDTO.getModuleTmplId() || LcdpConstant.MODULE_TMPL_UP_DOWN_ID == moduleTmplDTO.getModuleTmplId()) {
         gridConfig.put("dependentWidgetId", moduleTmplDTO.getDependentWidgetId());
      }

      copyGrid.setConfig(this.dealGridConfig(gridConfig));
      JSONObject gridToobarConfig = JSON.parseObject(copyGridToolbar.getConfig());
      List<LcdpModuleTmplPageCompBean> buttonCompList = gridCompList.stream().filter(comp -> "Button".equals(comp.getType())).collect(Collectors.toList());
      Map<String, String> buttonIdMap = new HashMap<>();
      List<LcdpModuleTmplPageCompBean> copyButtonList = buttonCompList.stream().map(button -> {
         LcdpModuleTmplPageCompBean copyButton = new LcdpModuleTmplPageCompBean();
         BeanUtils.copyProperties(button, copyButton);
         copyButton.setId(StringUtils.randomUUID());
         buttonIdMap.put(button.getId(), copyButton.getId());
         copyButton.setParentId(copyGridToolbar.getId());
         copyButton.setConfig(String.format(button.getConfig(), this.firstUppercase(tableDTO.getTableName().replaceFirst("T_", "").toLowerCase())[1]));
         return copyButton;
      }).collect(Collectors.toList());
      JSONArray gridToolbarChildrenWidgetId = gridToobarConfig.getJSONArray("childrenWidgetId");

      for (int i = 0; i < gridToolbarChildrenWidgetId.size(); i++) {
         gridToolbarChildrenWidgetId.set(i, buttonIdMap.get(gridToolbarChildrenWidgetId.getString(i)));
      }

      gridToobarConfig.put("childrenWidgetId", gridToolbarChildrenWidgetId);
      copyGridToolbar.setConfig(gridToobarConfig.toJSONString());
      newCompList.add(copyTabPanel);
      newCompList.add(copyGrid);
      newCompList.add(copyGridToolbar);
      newCompList.addAll(copyButtonList);
      newCompList.addAll(gridColumnList);
   }

   private String fieldToAddValidateConfig(LcdpTableFieldDTO field, String type) {
      JSONObject fieldConfig = null;
      if (StringUtils.equals("column", type)) {
         fieldConfig = JSON.parseObject(field.getExt$Item("columnconfig"));
      } else if (StringUtils.equals("field", type)) {
         fieldConfig = JSON.parseObject(field.getExt$Item("fieldconfig"));
      }

      String fieldType = field.getFieldType();
      if (StringUtils.equalsIgnoreCase("text", fieldType)) {
         String fieldLength = field.getFieldLength();
         List<String> validateList = new ArrayList<>();
         if (fieldLength != null && ConvertUtils.convert(fieldLength, Long.class, 0L) > 3L) {
            Long length = ConvertUtils.convert(fieldLength, Long.class);
            long validateLength = length / 3L;
            validateList.add("strLength[0," + validateLength + "]");
         }

         fieldConfig.put("validators", validateList.toArray());
      }

      return fieldConfig.toJSONString();
   }

   private void dealMasterGrid(
      LcdpModuleTmplDTO moduleTmplDTO,
      List<LcdpModuleTmplPageCompBean> newCompList,
      List<LcdpTableFieldDTO> masterFieldList,
      LcdpModuleTmplPageCompBean masterDataGrid
   ) {
      JSONObject masterGridConfig = JSON.parseObject(masterDataGrid.getConfig());
      String gridId = String.format(masterGridConfig.getString("id"), moduleTmplDTO.getModuleName().toLowerCase());
      LcdpModuleTmplPageCompBean processStatus = null;
      List<LcdpTableFieldDTO> dealFiledList = masterFieldList.stream().map(field -> {
         LcdpTableFieldDTO fieldDTO = new LcdpTableFieldDTO();
         BeanUtils.copyProperties(field, fieldDTO);
         return fieldDTO;
      }).collect(Collectors.toList());
      List<LcdpTableFieldDTO> createFieldList = dealFiledList.stream()
         .filter(mf -> LcdpConstant.CREATE_COLUMN_LIST.contains(mf.getFieldName().toUpperCase()))
         .collect(Collectors.toList());
      dealFiledList.removeAll(createFieldList);
      if (StringUtils.equals("1", moduleTmplDTO.getBpFlag())) {
         dealFiledList = dealFiledList.stream()
            .filter(field -> !StringUtils.equalsIgnoreCase("PROCESSSTATUS", field.getFieldName()))
            .collect(Collectors.toList());
         String fieldTmpl = LcdpModuleTempHelper.FIELD_TMPL_MAP.get("processStatus");
         String fieldConfig = String.format(fieldTmpl, moduleTmplDTO.getMasterTableName());
         processStatus = new LcdpModuleTmplPageCompBean();
         processStatus.setId(StringUtils.randomUUID());
         processStatus.setParentId(masterDataGrid.getId());
         processStatus.setType("GridColumn");
         processStatus.setPageType(masterDataGrid.getPageType());
         processStatus.setConfig(fieldConfig);
      }

      List<LcdpTableFieldDTO> columnConfigList = this.buildGridColumnConfigList(gridId, dealFiledList);
      columnConfigList.addAll(this.buildGridColumnConfigList(gridId, createFieldList));
      if (LcdpConstant.MODULE_TEPL_PENETRATION == moduleTmplDTO.getModuleTmplId() && !StringUtils.equals(masterDataGrid.getPageType(), "ChoosePage")) {
         LcdpTableFieldDTO field = columnConfigList.get(0);
         String fieldTmpl = LcdpModuleTempHelper.FIELD_TMPL_MAP.get("linkGridColumn");
         String fieldName = field.getFieldName().toLowerCase();
         String fieldConfig = String.format(fieldTmpl, fieldName, field.getFieldComment(), gridId + "_" + fieldName, fieldName);
         field.setExt$Item("columnconfig", fieldConfig);
      }

      List<LcdpModuleTmplPageCompBean> gridColumnList = new ArrayList<>();
      gridColumnList.addAll(this.bulidCommonColumns(masterDataGrid.getId(), masterDataGrid.getPageType()));
      if (processStatus != null) {
         gridColumnList.add(processStatus);
      }

      columnConfigList.forEach(filed -> {
         LcdpModuleTmplPageCompBean gridColumn = new LcdpModuleTmplPageCompBean();
         gridColumn.setId(StringUtils.randomUUID());
         gridColumn.setParentId(masterDataGrid.getId());
         gridColumn.setType("GridColumn");
         JSONObject columnConfig = JSON.parseObject(filed.getExt$Item("columnconfig"));
         columnConfig.put("editor", true);
         if (LcdpConstant.CREATE_COLUMN_LIST.contains(filed.getFieldName().toUpperCase())) {
            columnConfig.put("editor", false);
         }

         if (moduleTmplDTO.getModuleTmplId() != LcdpConstant.MODULE_TMPL_UP_DOWN_ID || !StringUtils.equals(masterDataGrid.getPageType(), "EditPage")) {
            columnConfig.put("editor", false);
         }

         gridColumn.setConfig(columnConfig.toJSONString());
         gridColumn.setPageType(masterDataGrid.getPageType());
         gridColumnList.add(gridColumn);
      });
      if (StringUtils.equals("ChoosePage", masterDataGrid.getPageType())) {
         gridColumnList.forEach(column -> {
            JSONObject columnConfig = JSON.parseObject(column.getConfig());
            if (columnConfig.get("checkbox") != null && columnConfig.getBoolean("checkbox")) {
               columnConfig.put("getSelectType__$S$__", moduleTmplDTO.getModuleName().toLowerCase() + "_choose_list_grid_selection_getselectiontype");
               column.setConfig(columnConfig.toJSONString());
            }
         });
      }

      gridColumnList.forEach(
         column -> {
            JSONObject columnConfig = JSON.parseObject(column.getConfig());
            if (!StringUtils.equals(columnConfig.getString("gridColumnType"), "checkbox")
               && !StringUtils.equals(columnConfig.getString("gridColumnType"), "index")) {
               JSONObject title = columnConfig.getJSONObject("title");
               title.put("type", "i18n");
               title.put("i18nCode", (moduleTmplDTO.getMasterTableName() + "." + columnConfig.getString("field")).toUpperCase());
               title.remove("en-US");
               columnConfig.put("title", title);
               column.setConfig(columnConfig.toJSONString());
            }
         }
      );
      List<String> gridColumnIdList = gridColumnList.stream().map(LcdpModuleTmplPageCompBean::getId).collect(Collectors.toList());
      masterGridConfig.put("childrenWidgetId", gridColumnIdList.toArray());
      newCompList.addAll(gridColumnList);
      masterGridConfig.put("id", gridId);
      String urlPath = moduleTmplDTO.getModulePath()
         + "server.Lcdp"
         + this.firstUppercase(moduleTmplDTO.getMasterTableName().replaceFirst("T_", "").toLowerCase())[1]
         + "Service";
      String gridUrl = String.format(masterGridConfig.getString("url"), urlPath);
      if ("1".equals(moduleTmplDTO.getBpFlag())) {
         String urlSuffix = "";
         String var16 = masterDataGrid.getPageType();
         switch (var16) {
            case "EditPage":
               urlSuffix = ".selectRawPagination";
               break;
            case "AuditPage":
               urlSuffix = ".selectAuditablePagination";
               break;
            case "SearchPage":
               urlSuffix = ".selectSearchablePagination";
               break;
            case "ChoosePage":
               urlSuffix = ".selectChoosablePaginationData";
         }

         gridUrl = urlPath + urlSuffix;
      }

      masterGridConfig.put("url", gridUrl);
      String instantSavePath = urlPath + ".updateData";
      masterGridConfig.put("instantSavePath", instantSavePath);
      masterGridConfig.put("getRequestData__$S$__", "getGridRequestData");
      masterGridConfig.put("onRowActive__$S$__", "");
      masterGridConfig.put("onLoadSuccess__$S$__", "");
      masterGridConfig.put("confirmField", columnConfigList.stream().findFirst().get().getFieldName().toLowerCase());
      masterDataGrid.setConfig(this.dealGridConfig(masterGridConfig));
      moduleTmplDTO.setDependentWidgetId(gridId);
   }

   private void dealMasterForm(
      LcdpModuleTmplDTO moduleTmplDTO,
      List<LcdpModuleTmplPageCompBean> newCompList,
      List<LcdpTableFieldDTO> masterFieldList,
      LcdpModuleTmplPageCompBean formComp
   ) {
      JSONObject formConfig = JSON.parseObject(formComp.getConfig());
      String formId = String.format(formConfig.getString("id"), moduleTmplDTO.getModuleName().toLowerCase());
      List<LcdpTableFieldDTO> fieldDTOList = this.buildFormFieldConfigList(formId, masterFieldList);
      List<LcdpModuleTmplPageCompBean> formFieldList = new ArrayList<>();
      fieldDTOList.forEach(fieldDTO -> {
         LcdpModuleTmplPageCompBean formField = new LcdpModuleTmplPageCompBean();
         formField.setId(StringUtils.randomUUID());
         formField.setParentId(formComp.getId());
         formField.setType(fieldDTO.getFieldType());
         formField.setConfig(this.fieldToAddValidateConfig(fieldDTO, "field"));
         JSONObject fieldConfig = JSON.parseObject(formField.getConfig());
         if (LcdpConstant.CREATE_COLUMN_LIST.contains(fieldDTO.getFieldName().toUpperCase())) {
            fieldConfig.put("readonly", true);
            formField.setConfig(fieldConfig.toJSONString());
         }

         JSONObject title = fieldConfig.getJSONObject("title");
         title.put("type", "i18n");
         title.put("i18nCode", (moduleTmplDTO.getMasterTableName() + "." + fieldConfig.getString("field")).toUpperCase());
         title.remove("en-US");
         fieldConfig.put("title", title);
         formField.setConfig(fieldConfig.toJSONString());
         formField.setPageType(formComp.getPageType());
         formFieldList.add(formField);
      });
      newCompList.addAll(formFieldList);
      List<String> formFieldIdList = formFieldList.stream().map(LcdpModuleTmplPageCompBean::getId).collect(Collectors.toList());
      formConfig.put("childrenWidgetId", formFieldIdList.toArray());
      formConfig.put("id", formId);
      String urlPath = moduleTmplDTO.getModulePath()
         + "server.Lcdp"
         + this.firstUppercase(moduleTmplDTO.getMasterTableName().replaceFirst("T_", "").toLowerCase())[1]
         + "Service";
      String formUrl = String.format(formConfig.getString("url"), urlPath);
      formConfig.put("url", formUrl);
      String formInstantSavePath = String.format(formConfig.getString("instantSavePath"), urlPath);
      formConfig.put("instantSavePath", formInstantSavePath);
      if (LcdpConstant.MODULE_TMPL_LEFT_RIGHT_ID == moduleTmplDTO.getModuleTmplId()) {
         formConfig.put("dependentWidgetId", moduleTmplDTO.getDependentWidgetId());
         formConfig.put("onUpdated__$S$__", "onUpdateFormData");
      }

      formComp.setConfig(this.dealFormConfig(formConfig));
      if (StringUtils.isEmpty(moduleTmplDTO.getDependentWidgetId())) {
         moduleTmplDTO.setDependentWidgetId(formId);
      }
   }

   private List<LcdpTableFieldDTO> buildFormFieldConfigList(String moduleName, List<LcdpTableFieldDTO> masterFieldList) {
      for (LcdpTableFieldDTO tableFieldDTO : masterFieldList) {
         String fieldType = tableFieldDTO.getFieldType();
         if ("varchar".equalsIgnoreCase(fieldType) || "clob".equalsIgnoreCase(fieldType)) {
            tableFieldDTO.setFieldType("text");
         }

         tableFieldDTO.setFieldType(this.firstUppercase(tableFieldDTO.getFieldType())[1]);
         String fieldTmpl = LcdpModuleTempHelper.FIELD_TMPL_MAP.get(tableFieldDTO.getFieldType());
         String fieldName = tableFieldDTO.getFieldName().toLowerCase();
         String fieldConfig = String.format(fieldTmpl, fieldName, tableFieldDTO.getFieldComment(), fieldName, moduleName + "_" + fieldName);
         tableFieldDTO.setExt$Item("fieldconfig", fieldConfig);
      }

      return masterFieldList;
   }

   private List<LcdpTableFieldDTO> buildGridColumnConfigList(String moduleName, List<LcdpTableFieldDTO> masterFieldList) {
      for (LcdpTableFieldDTO tableFieldDTO : masterFieldList) {
         String fieldType = tableFieldDTO.getFieldType();
         if ("varchar".equalsIgnoreCase(fieldType) || "clob".equalsIgnoreCase(fieldType) || "number".equalsIgnoreCase(fieldType)) {
            tableFieldDTO.setFieldType("text");
         }

         String fieldTmpl = LcdpModuleTempHelper.FIELD_TMPL_MAP.get(tableFieldDTO.getFieldType() + "GridColumn");
         String fieldName = tableFieldDTO.getFieldName().toLowerCase();
         String fieldConfig = String.format(fieldTmpl, fieldName, tableFieldDTO.getFieldComment(), moduleName + "_" + fieldName, fieldName);
         tableFieldDTO.setExt$Item("columnconfig", fieldConfig);
      }

      return masterFieldList;
   }

   private List<LcdpModuleTmplPageCompBean> bulidCommonColumns(String parentId, String pageType) {
      List<LcdpModuleTmplPageCompBean> gridColumnList = new ArrayList<>();
      LcdpModuleTmplPageCompBean checkBox = new LcdpModuleTmplPageCompBean();
      checkBox.setId(StringUtils.randomUUID());
      checkBox.setParentId(parentId);
      checkBox.setType("GridColumn");
      checkBox.setPageType(pageType);
      checkBox.setConfig(LcdpModuleTempHelper.FIELD_TMPL_MAP.get("checkbox"));
      LcdpModuleTmplPageCompBean index = new LcdpModuleTmplPageCompBean();
      index.setId(StringUtils.randomUUID());
      index.setParentId(parentId);
      index.setType("GridColumn");
      index.setPageType(pageType);
      index.setConfig(LcdpModuleTempHelper.FIELD_TMPL_MAP.get("index"));
      gridColumnList.add(checkBox);
      gridColumnList.add(index);
      return gridColumnList;
   }

   private String generatePageContent(String pageType, LcdpModuleTmplDTO moduleTmplDTO) {
      TemplateEngine engine = new TemplateEngine();
      ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
      if (LcdpConstant.MODULE_TMPL_LEFT_RIGHT_ID == moduleTmplDTO.getModuleTmplId()) {
         resolver.setPrefix("thymeleaf/left-right/");
      } else if (LcdpConstant.MODULE_TEPL_PENETRATION == moduleTmplDTO.getModuleTmplId()) {
         resolver.setPrefix("thymeleaf/penetration/");
         moduleTmplDTO.setDetailPagePath(moduleTmplDTO.getModulePath() + "page." + moduleTmplDTO.getModuleName() + "DetailPage");
      } else {
         resolver.setPrefix("thymeleaf/up-down/");
      }

      resolver.setSuffix(".js");
      resolver.setTemplateMode("TEXT");
      resolver.setCharacterEncoding("UTF-8");
      engine.setTemplateResolver(resolver);
      Context context = new Context();
      LcdpModuleTmplDTO dealDTO = new LcdpModuleTmplDTO();
      BeanUtils.copyProperties(moduleTmplDTO, dealDTO);
      dealDTO.setMasterTableName(dealDTO.getMasterTableName().toLowerCase().replaceAll("_", "-"));
      List<LcdpModuleTmplChildTableDTO> childTableList = dealDTO.getChildTableList();
      if (!childTableList.isEmpty()) {
         for (LcdpModuleTmplChildTableDTO child : childTableList) {
            child.setScriptPath(
               moduleTmplDTO.getModulePath() + "server.Lcdp" + this.firstUppercase(child.getTableName().replaceFirst("T_", "").toLowerCase())[1] + "Service"
            );
            child.setCompName(child.getTableName().toLowerCase().replaceFirst("t_", "").replaceAll("_", "-"));
            child.setFunctionName(this.firstUppercase(child.getTableName().replaceFirst("T_", "").toLowerCase())[1]);
            child.setAssociatedField(child.getAssociatedField().toLowerCase());
            child.setChildUrlName("Lcdp" + this.firstUppercase(child.getTableName().replaceFirst("T_", "").toLowerCase())[1]);
         }
      }

      dealDTO.setBpFlag("1".equals(dealDTO.getBpFlag()) ? "true" : "false");
      dealDTO.setUploaderFlag("1".equals(dealDTO.getUploaderFlag()) ? "true" : "false");
      dealDTO.setPreInsertFlag("1".equals(dealDTO.getPreInsertFlag()) ? "true" : "false");
      dealDTO.setModuleName(dealDTO.getModuleName().toLowerCase());
      String masterTableName = moduleTmplDTO.getMasterTableName();
      LcdpTableDTO tableDTO = this.lcdpTableService.selectPhysicalTableInfo(masterTableName);
      LcdpTableFieldDTO preInsertDTO = tableDTO.getFieldList().size() > 1
         ? tableDTO.getFieldList().stream().skip(1L).findFirst().get()
         : tableDTO.getFieldList().stream().findFirst().get();
      LcdpTableFieldDTO linkDTO = tableDTO.getFieldList().size() > 1
         ? tableDTO.getFieldList().stream().skip(1L).findFirst().get()
         : tableDTO.getFieldList().stream().findFirst().get();
      linkDTO.setFieldName(linkDTO.getFieldName().toLowerCase());
      context.setVariable("moduleTmpl", dealDTO);
      context.setVariable("preInsert", preInsertDTO);
      context.setVariable("masterTableName", moduleTmplDTO.getMasterTableName());
      context.setVariable("link", linkDTO);
      if (LcdpConstant.MODULE_TMPL_LEFT_RIGHT_ID == moduleTmplDTO.getModuleTmplId()) {
         context.setVariable("preInsertFormPath", dealDTO.getModulePath() + "page." + dealDTO.getResourceName() + pageType);
      } else {
         context.setVariable("preInsertFormPath", dealDTO.getModulePath() + "page." + dealDTO.getResourceName() + "DetailPage");
      }

      return engine.process(pageType, context);
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

   private boolean noReturnDataFilter(LcdpResourceBean resourceBean) {
      return LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resourceBean.getResourceCategory())
         && ObjectUtils.isEmpty(resourceBean.getEffectVersion())
         && !LocalContextHelper.getLoginUserId().equals(resourceBean.getCreatedById());
   }

   private String dealGridConfig(JSONObject gridConfig) {
      gridConfig.put("sort", true);
      gridConfig.put("page", true);
      gridConfig.put("serverSearch", true);
      gridConfig.put("cellValueFillByDrag", false);
      gridConfig.put("showCheckedBadgeGt", 1);
      gridConfig.put("contextMenuConfig", false);
      gridConfig.remove("filterOpen");
      gridConfig.remove("toolbarAlign");
      gridConfig.remove("editorInvisible");
      gridConfig.remove("columnsFill");
      gridConfig.remove("contentAlign");
      gridConfig.remove("checkOnActive");
      gridConfig.remove("checkOneOnActive");
      gridConfig.remove("scroll");
      gridConfig.remove("badgeOverflowCount");
      gridConfig.remove("checkContinuous");
      return gridConfig.toJSONString();
   }

   private String dealFormConfig(JSONObject formConfig) {
      formConfig.put("layout", "inline");
      formConfig.put("titleAlign", "right");
      return formConfig.toJSONString();
   }

   private void addCompDocument(
      List<LcdpResourceBean> resourceList, List<LcdpResourceHistoryBean> resourceHistoryList, List<LcdpResourceDocumentBean> documentList
   ) {
      List<Long> resourceIdList = resourceList.stream().filter(f -> "view".equals(f.getResourceCategory())).map(e -> e.getId()).collect(Collectors.toList());
      List<Long> resourceHistoryIdList = resourceHistoryList.stream()
         .filter(f -> "view".equals(f.getResourceCategory()))
         .map(e -> e.getId())
         .collect(Collectors.toList());
      List<LcdpModulePageCompBean> resourceCompList = this.lcdpModulePageCompService
         .selectListByFilter(SearchFilter.instance().match("MODULEPAGEID", resourceIdList).filter(MatchPattern.OR), new Order[0]);
      List<LcdpModulePageCompBean> resourceHistoryCompList = this.lcdpModulePageCompService
         .selectListByFilter(SearchFilter.instance().match("MODULEPAGEHISTORYID", resourceHistoryIdList).filter(MatchPattern.OR), new Order[0]);

      for (LcdpModulePageCompBean comp : resourceCompList) {
         LcdpResourceBean resource = resourceList.stream()
            .filter(f -> ObjectUtils.equals(f.getId(), comp.getModulePageId()))
            .findFirst()
            .orElse(new LcdpResourceBean());
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

      for (LcdpModulePageCompBean historyComp : resourceHistoryCompList) {
         LcdpResourceHistoryBean historyResource = resourceHistoryList.stream()
            .filter(f -> ObjectUtils.equals(f.getId(), historyComp.getModulePageHistoryId()))
            .findFirst()
            .orElse(new LcdpResourceHistoryBean());
         String content = historyComp.getConfig() + (StringUtils.isEmpty(historyComp.getConfigExtend()) ? "" : historyComp.getConfigExtend());
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
}
