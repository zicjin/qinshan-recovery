package com.sunwayworld.cloud.module.lcdp.resourceversion.service.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceHistoryService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.resourceversion.persistent.dao.LcdpResourceVersionDao;
import com.sunwayworld.cloud.module.lcdp.resourceversion.service.LcdpResourceVersionService;
import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.service.LcdpSubmitLogService;
import com.sunwayworld.cloud.module.lcdp.support.constant.LcdpConstant;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpViewService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpResourceVersionServiceImpl implements LcdpResourceVersionService {
   @Autowired
   @Lazy
   private LcdpResourceService lcdpResourceService;
   @Autowired
   private LcdpResourceVersionDao lcdpResourceVersionDao;
   @Autowired
   private LcdpResourceLockService lcdpResourceLockService;
   @Autowired
   private LcdpResourceHistoryService lcdpResourceHistoryService;
   @Autowired
   private LcdpTableService lcdpTableService;
   @Lazy
   @Autowired
   private LcdpSubmitLogService submitLogService;
   @Autowired
   private LcdpViewService lcdpViewService;

   public LcdpResourceVersionDao getDao() {
      return this.lcdpResourceVersionDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void submitResource(RestJsonWrapperBean wrapper, Long logId) {
      List<LcdpResourceVersionBean> resourceVersionList = wrapper.parse(LcdpResourceVersionBean.class);
      List<String> hisResourceIdList = resourceVersionList.stream().map(LcdpResourceVersionBean::getResourceId).collect(Collectors.toList());
      List<LcdpResourceVersionBean> hisResourceVesionList = this.selectListByFilter(
         SearchFilter.instance().match("RESOURCEID", hisResourceIdList).filter(MatchPattern.OR), new Order[0]
      );
      Map<String, List<LcdpResourceVersionBean>> resourceId2HistroyMap = hisResourceVesionList.stream()
         .collect(Collectors.groupingBy(LcdpResourceVersionBean::getResourceId));
      hisResourceVesionList.forEach(e -> e.setEffectFlag("no"));
      this.getDao().update(hisResourceVesionList, new String[]{"EFFECTFLAG"});
      List<String> updateResourceIdList = new ArrayList<>();
      List<String> updateTableNameList = new ArrayList<>();
      List<String> updateViewNameList = new ArrayList<>();
      resourceVersionList.forEach(version -> {
         if ("table".equals(version.getResourceCategory())) {
            updateTableNameList.add(version.getResourceId());
         } else if ("db-view".equals(version.getResourceCategory())) {
            updateViewNameList.add(version.getResourceId());
         } else {
            updateResourceIdList.add(version.getResourceId());
         }
      });
      SearchFilter resourceFilter = SearchFilter.instance()
         .match("RESOURCEID", updateResourceIdList)
         .filter(MatchPattern.OR)
         .match("SUBMITFLAG", "no")
         .filter(MatchPattern.EQ);
      if (!StringUtils.equals(LocalContextHelper.getLoginUser().getUserType(), "sysAdmin")) {
         resourceFilter.match("CREATEDBYID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.SEQ);
      }

      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService.selectListByFilter(resourceFilter, new Order[0]);
      Map<String, Long> resourceVersionByTableNameMap = resourceHistoryList.stream()
         .collect(Collectors.toMap(resource -> resource.getResourceId().toString(), LcdpResourceHistoryBean::getVersion, (v1, v2) -> v2));
      Map<String, LocalDateTime> id2CreatedTimeMap = resourceHistoryList.stream()
         .collect(Collectors.toMap(resource -> resource.getResourceId().toString(), LcdpResourceHistoryBean::getCreatedTime, (v1, v2) -> v2));
      SearchFilter tableFilter = SearchFilter.instance()
         .match("TABLENAME", updateTableNameList)
         .filter(MatchPattern.OR)
         .match("SUBMITFLAG", "no")
         .filter(MatchPattern.EQ);
      if (!StringUtils.equals(LocalContextHelper.getLoginUser().getUserType(), "sysAdmin")) {
         tableFilter.match("CREATEDBYID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.SEQ);
      }

      List<LcdpTableBean> tableList = this.lcdpTableService.selectListByFilter(tableFilter, new Order[0]);
      SearchFilter viewFilter = SearchFilter.instance()
         .match("TABLENAME", updateViewNameList)
         .filter(MatchPattern.OR)
         .match("SUBMITFLAG", "no")
         .filter(MatchPattern.EQ);
      if (!StringUtils.equals(LocalContextHelper.getLoginUser().getUserType(), "sysAdmin")) {
         tableFilter.match("CREATEDBYID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.SEQ);
      }

      List<LcdpViewBean> viewList = this.lcdpViewService.selectListByFilter(viewFilter, new Order[0]);
      id2CreatedTimeMap.putAll(tableList.stream().collect(Collectors.toMap(LcdpTableBean::getTableName, LcdpTableBean::getCreatedTime, (v1, v2) -> v2)));
      id2CreatedTimeMap.putAll(viewList.stream().collect(Collectors.toMap(LcdpViewBean::getTableName, LcdpViewBean::getCreatedTime, (v1, v2) -> v2)));
      Map<String, Long> tableVersionByTableNameMap = tableList.stream()
         .collect(Collectors.toMap(LcdpTableBean::getTableName, LcdpTableBean::getVersion, (v1, v2) -> v2));
      Map<String, Long> viewVersionByTableNameMap = viewList.stream()
         .collect(Collectors.toMap(LcdpViewBean::getTableName, LcdpViewBean::getVersion, (v1, v2) -> v2));
      resourceVersionList.forEach(version -> {
         version.setId(ApplicationContextHelper.getNextIdentity());
         version.setLogId(logId);
         version.setVersion(resourceVersionByTableNameMap.get(version.getResourceId()));
         if ("table".equals(version.getResourceCategory())) {
            version.setVersion(tableVersionByTableNameMap.get(version.getResourceId()));
         } else if ("db-view".equals(version.getResourceCategory())) {
            version.setVersion(viewVersionByTableNameMap.get(version.getResourceId()));
         }

         version.setEditTime(id2CreatedTimeMap.get(version.getResourceId()));
      });
      List<Long> resourceIdList = updateResourceIdList.stream().map(Long::valueOf).collect(Collectors.toList());
      List<LcdpResourceBean> resourceList = this.lcdpResourceService.selectListByIds(resourceIdList);
      resourceList.forEach(resource -> {
         String[] path = resource.getPath().split("\\.");
         resource.setExt$Item("categoryName", path[0]);
         resource.setExt$Item("moduleName", path[1]);
      });
      Map<Long, LcdpResourceBean> resourceMap = resourceList.stream().collect(Collectors.toMap(LcdpResourceBean::getId, Function.identity()));
      resourceVersionList.stream()
         .forEach(
            version -> {
               if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(version.getResourceCategory())) {
                  LcdpResourceBean r = resourceMap.get(Long.valueOf(version.getResourceId()));
                  version.setCategoryName(r.getExt$Item("categoryName"));
                  version.setModuleName(r.getExt$Item("moduleName"));
                  LcdpResourceHistoryBean historyBean = resourceHistoryList.stream()
                     .filter(f -> ObjectUtils.equals(r.getId(), f.getResourceId()))
                     .findFirst()
                     .orElse(null);
                  if (historyBean != null) {
                     version.setResourcePath(historyBean.getPath().replaceAll("\\.", "/"));
                  } else {
                     version.setResourcePath(r.getPath().replaceAll("\\.", "/"));
                  }
               }

               if ("table".equals(version.getResourceCategory()) || "db-view".equals(version.getResourceCategory())) {
                  version.setResourcePath(version.getResourceName());
               }

               version.setResourceCategory(LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get(version.getResourceCategory()));
               List<LcdpResourceVersionBean> resourceVersionValue = resourceId2HistroyMap.get(version.getResourceId());
               version.setResourceAction("update");
               if (resourceVersionValue == null) {
                  version.setResourceAction("new");
               }
            }
         );
      this.lcdpViewService.submit(viewList);
      this.lcdpTableService.submit(tableList);
      this.getDao().insert(resourceVersionList);
      this.lcdpResourceService.submitResource(updateResourceIdList);
      this.lcdpResourceLockService.unLock(hisResourceIdList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void activateResource(RestJsonWrapperBean wrapper) {
      LcdpResourceVersionBean resourceVersion = wrapper.parseUnique(LcdpResourceVersionBean.class);
      if (!"table".equals(resourceVersion.getResourceCategory())) {
         String resourceId = resourceVersion.getResourceId();
         Long activateVersion = resourceVersion.getVersion();
         List<LcdpResourceVersionBean> versionList = this.selectListByFilter(
            SearchFilter.instance().match("RESOURCEID", resourceId).filter(MatchPattern.EQ), new Order[0]
         );
         versionList.stream().forEach(version -> {
            version.setEffectFlag("no");
            if (activateVersion == version.getVersion()) {
               version.setEffectFlag("yes");
            }
         });
         this.getDao().update(versionList, new String[]{"EFFECTFLAG"});
         this.lcdpResourceService.activateResource(resourceId, activateVersion);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertByHistoryResourceList(List<LcdpResourceHistoryBean> resourceHistoryList, LcdpSubmitLogBean submitLog) {
      if (!resourceHistoryList.isEmpty()) {
         List<LcdpResourceVersionBean> insertVersionList = resourceHistoryList.stream().map(history -> {
            LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
            resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
            resourceVersion.setLogId(submitLog.getId());
            resourceVersion.setResourceId(history.getResourceId().toString());
            resourceVersion.setResourceName(history.getResourceName());
            resourceVersion.setResourceCategory(LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get(history.getResourceCategory()));
            resourceVersion.setEffectFlag("yes");
            resourceVersion.setVersion(history.getVersion());
            resourceVersion.setCommit(submitLog.getCommit());
            resourceVersion.setEditTime(history.getCreatedTime());
            String[] path = history.getPath().split("\\.");
            resourceVersion.setCategoryName(path[0]);
            resourceVersion.setModuleName(path[1]);
            resourceVersion.setResourceAction(history.getVersion() == 1L ? "new" : "update");
            resourceVersion.setResourcePath(history.getPath().replaceAll("\\.", "/"));
            EntityHelper.assignCreatedElement(resourceVersion);
            return resourceVersion;
         }).collect(Collectors.toList());
         this.getDao().insert(insertVersionList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertByTableList(List<LcdpTableBean> tableList, LcdpSubmitLogBean submitLog) {
      if (!tableList.isEmpty()) {
         List<LcdpResourceVersionBean> insertVersionList = tableList.stream().map(table -> {
            LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
            resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
            resourceVersion.setLogId(submitLog.getId());
            resourceVersion.setResourceId(table.getTableName());
            resourceVersion.setResourceName(table.getTableName());
            resourceVersion.setResourcePath(table.getTableName());
            resourceVersion.setResourceCategory(LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get("table"));
            resourceVersion.setEffectFlag("yes");
            resourceVersion.setVersion(table.getVersion());
            resourceVersion.setCommit(submitLog.getCommit());
            resourceVersion.setEditTime(table.getCreatedTime());
            resourceVersion.setResourcePath(table.getTableName());
            EntityHelper.assignCreatedElement(resourceVersion);
            return resourceVersion;
         }).collect(Collectors.toList());
         this.getDao().insert(insertVersionList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void dealHistoryData(RestJsonWrapperBean wrapper) {
      List<LcdpSubmitLogBean> submitLogList = new ArrayList<>();
      List<LcdpResourceVersionBean> historyVersionList = this.selectListByFilter(
         SearchFilter.instance().match("LogId", null).filter(MatchPattern.EQ), new Order[0]
      );
      List<Long> resourceIdList = historyVersionList.stream()
         .filter(version -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(version.getResourceCategory()))
         .map(LcdpResourceVersionBean::getResourceId)
         .map(Long::valueOf)
         .distinct()
         .collect(Collectors.toList());
      List<LcdpResourceBean> resourceList = this.lcdpResourceService.selectListByIds(resourceIdList);
      Map<Long, LcdpResourceBean> resourceMap = resourceList.stream().collect(Collectors.toMap(LcdpResourceBean::getId, Function.identity()));
      historyVersionList.forEach(
         version -> {
            if (version.getResourceCategory().equals("table")) {
               version.setResourceCategory("table");
               version.setResourcePath("table");
            } else {
               if (null == resourceMap.get(Long.valueOf(version.getResourceId()))) {
                  return;
               }

               version.setResourceDeleteFlag(resourceMap.get(Long.valueOf(version.getResourceId())).getDeleteFlag());
               version.setResourcePath(resourceMap.get(Long.valueOf(version.getResourceId())).getPath().replaceAll("\\.", "/"));
               version.setResourceCategory(
                  LcdpConstant.FILE_TYPE_UNDER_FOLDER_MAP.get(resourceMap.get(Long.valueOf(version.getResourceId())).getResourceCategory())
               );
            }

            version.setLogId(ApplicationContextHelper.getNextIdentity());
            LcdpSubmitLogBean submitLog = new LcdpSubmitLogBean();
            submitLog.setId(version.getLogId());
            submitLog.setCommit(version.getCommit());
            submitLog.setCreatedByOrgName("sourceDealData");
            submitLogList.add(submitLog);
         }
      );
      this.getDao().update(historyVersionList, new String[]{"RESOURCEDELETEFLAG", "RESOURCEPATH", "RESOURCECATEGORY", "LOGID"});
      this.submitLogService.getDao().insert(submitLogList);
   }

   @Override
   public Page<LcdpResourceVersionBean> selectPagination(RestJsonWrapperBean wrapper) {
      Page<LcdpResourceVersionBean> resourceVersionPage = LcdpResourceVersionService.super.selectPagination(wrapper);
      List<LcdpResourceVersionBean> resourceVersionList = resourceVersionPage.getRows();
      String resourceId = wrapper.getFilterValue("resourceId_EQ");
      List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
         .selectListByFilter(
            SearchFilter.instance().match("RESOURCEID", resourceId).filter(MatchPattern.EQ).match("SUBMITFLAG", "no").filter(MatchPattern.EQ),
            new Order[]{Order.desc("VERSION")}
         );
      if (!resourceHistoryList.isEmpty()) {
         LcdpResourceHistoryBean resourceHistory = resourceHistoryList.get(0);
         LcdpResourceVersionBean version = new LcdpResourceVersionBean();
         version.setResourceName(resourceHistory.getResourceName());
         version.setResourceId(resourceHistory.getResourceId().toString());
         version.setVersion(resourceHistory.getVersion());
         version.setEffectFlag("no");
         version.setCreatedById(resourceHistory.getCreatedById());
         version.setCreatedByName(resourceHistory.getCreatedByName());
         version.setEditTime(resourceHistory.getCreatedTime());
         resourceVersionList.add(0, version);
      }

      resourceVersionPage.setRows(resourceVersionList);
      return resourceVersionPage;
   }
}
