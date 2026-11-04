package com.sunwayworld.cloud.module.lcdp.table.service.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpAnalysisResultDTO;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.resourceversion.service.LcdpResourceVersionService;
import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.service.LcdpSubmitLogService;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableCompareDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpViewDao;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpDatabaseService;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpViewService;
import com.sunwayworld.cloud.module.lcdp.table.validator.LcdpViewDeleteDataValidator;
import com.sunwayworld.cloud.module.lcdp.table.validator.LcdpViewSaveDataValidator;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpViewServiceImpl implements LcdpViewService {
   @Autowired
   private LcdpViewDao lcdpViewDao;
   @Autowired
   private LcdpDatabaseService lcdpDatabaseService;
   @Autowired
   private LcdpResourceLockService lcdpResourceLockService;
   @Autowired
   @Lazy
   private LcdpResourceVersionService lcdpResourceVersionService;
   @Autowired
   private LcdpResourceCheckoutRecordService resourceCheckoutRecordService;
   @Autowired
   @Lazy
   private LcdpSubmitLogService submitLogService;

   public LcdpViewDao getDao() {
      return this.lcdpViewDao;
   }

   @Override
   public List<LcdpViewBean> selectPhysicalViewInfoList(RestJsonWrapperBean jsonWrapper) {
      return this.lcdpDatabaseService.selectPhysicalViewInfoList(jsonWrapper.extractMapFilter());
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @ValidateDataWith(LcdpViewSaveDataValidator.class)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LcdpViewBean lcdpView = jsonWrapper.parseUnique(LcdpViewBean.class);
      this.lcdpResourceLockService.lock(lcdpView.getViewName(), "db-view");
      lcdpView.setId(ApplicationContextHelper.getNextIdentity());
      lcdpView.setTableName(lcdpView.getViewName());
      lcdpView.setSubmitFlag("no");
      lcdpView.setVersion(1L);
      this.getDao().insert(lcdpView);
      this.resourceCheckoutRecordService.checkoutTableOrView(lcdpView.getViewName(), lcdpView.getViewName(), "db-view");
      return lcdpView.getId();
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long design(String viewName) {
      LcdpViewBean maxVersionTable = this.selectFirstByFilter(
         SearchFilter.instance().match("TABLENAME", viewName).filter(MatchPattern.EQ), new Order[]{Order.desc("VERSION")}
      );
      if (!ObjectUtils.isEmpty(maxVersionTable) && !"yes".equals(maxVersionTable.getSubmitFlag())) {
         return maxVersionTable.getId();
      } else {
         throw new CheckedException("LCDP.MODULE.TABLES.TABLE.EXCEPTION.LOCKED");
      }
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void checkout(String viewName) {
      this.lcdpResourceLockService.lock(viewName, "db-view");
      LcdpViewBean maxVersionView = this.selectFirstByFilter(
         SearchFilter.instance().match("VIEWNAME", viewName).filter(MatchPattern.SEQ), new Order[]{Order.desc("VERSION")}
      );
      LcdpViewBean lcdpViewBean = this.lcdpDatabaseService.selectPhysicalViewInfo(viewName);
      LcdpViewBean newVersionView = new LcdpViewBean();
      newVersionView.setId(ApplicationContextHelper.getNextIdentity());
      newVersionView.setTableName(viewName);
      newVersionView.setViewName(viewName);
      newVersionView.setSubmitFlag("no");
      newVersionView.setSelectStatement(lcdpViewBean.getSelectStatement());
      if (ObjectUtils.isEmpty(maxVersionView)) {
         newVersionView.setVersion(2L);
      } else {
         newVersionView.setVersion(Optional.ofNullable(maxVersionView.getVersion()).orElse(1L) + 1L);
      }

      this.resourceCheckoutRecordService.checkoutTableOrView(viewName, newVersionView.getViewName(), "db-view");
      this.getDao().insert(newVersionView);
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void submit(List<LcdpViewBean> viewList) {
      if (!viewList.isEmpty()) {
         List<LcdpViewBean> physicalViewList = this.lcdpDatabaseService
            .selectPhysicalViewInfoList(viewList.stream().map(LcdpViewBean::getViewName).collect(Collectors.toList()));
         List<LcdpViewBean> submitViewList = this.selectListByFilter(
            SearchFilter.instance().match("ID", viewList.stream().map(LcdpViewBean::getId).collect(Collectors.toList())).filter(MatchPattern.OR), new Order[0]
         );
         viewList.forEach(
            view -> {
               LcdpViewBean matchPhysicalView = physicalViewList.stream()
                  .filter(physicalView -> StringUtils.equalsIgnoreCase(view.getViewName(), physicalView.getViewName()))
                  .findFirst()
                  .orElse(null);
               LcdpViewBean matchSubmitView = submitViewList.stream()
                  .filter(submitView -> StringUtils.equalsIgnoreCase(view.getViewName(), submitView.getViewName()))
                  .findFirst()
                  .orElse(null);
               if (!ObjectUtils.isEmpty(matchSubmitView)) {
                  RedisHelper.evict("TC", view.getViewName());
                  RedisHelper.evict("EC", view.getViewName());
                  this.lcdpDatabaseService.testSelectStatement(view.getSelectStatement());
                  if (ObjectUtils.isEmpty(matchPhysicalView)) {
                     this.lcdpDatabaseService.createPhysicalView(matchSubmitView);
                  } else {
                     this.lcdpDatabaseService.alterPhysicalView(matchSubmitView);
                  }

                  view.setSubmitFlag("yes");
               }
            }
         );
         this.resourceCheckoutRecordService.unCheckoutTableOrView(viewList.stream().map(LcdpViewBean::getViewName).collect(Collectors.toList()));
         this.getDao().update(viewList, new String[]{"SUBMITFLAG"});
      }
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public List<String> revert(List<String> viewName) {
      List<LcdpViewBean> notSubmittedViewList = this.selectListByFilter(
         SearchFilter.instance()
            .match("VIEWNAME", viewName)
            .filter(MatchPattern.OR)
            .match("SUBMITFLAG", "no")
            .filter(MatchPattern.SEQ)
            .match("CREATEDBYID", LocalContextHelper.getLoginUserId())
            .filter(MatchPattern.SEQ)
            .match("VERSION", 1)
            .filter(MatchPattern.DIFFER),
         new Order[0]
      );
      this.getDao().deleteBy(notSubmittedViewList, new String[0]);
      this.resourceCheckoutRecordService.unCheckoutTableOrView(viewName);
      return notSubmittedViewList.stream().map(LcdpViewBean::getViewName).collect(Collectors.toList());
   }

   @Override
   public LcdpTableCompareDTO<LcdpViewBean> compare(RestJsonWrapperBean wrapper) {
      String tableName = wrapper.getParamValue("TABLENAME");
      Long version = Optional.ofNullable(wrapper.getParamValue("VERSION")).map(Long::valueOf).orElse(1L);
      LcdpTableCompareDTO<LcdpViewBean> differ = new LcdpTableCompareDTO<>();
      LcdpViewBean currentView = new LcdpViewBean();
      LcdpViewBean previousView = new LcdpViewBean();
      List<LcdpViewBean> historyViewList = this.selectListByFilter(
         SearchFilter.instance().match("VIEWNAME", tableName).filter(MatchPattern.SEQ).match("SUBMITFLAG", "yes").filter(MatchPattern.SEQ), new Order[0]
      );
      LcdpViewBean currentViewBean = historyViewList.stream().filter(table -> version == table.getVersion()).findAny().orElse(null);
      LcdpViewBean previousViewBean = historyViewList.stream().filter(table -> version == table.getVersion() + 1L).findAny().orElse(null);
      BeanUtils.copyProperties(currentViewBean, currentView);
      BeanUtils.copyProperties(previousViewBean, previousView);
      differ.setHistoryTableList(historyViewList);
      differ.setCurrentTable(currentView);
      differ.setPreviousTable(previousView);
      return differ;
   }

   @Override
   public Map<String, LcdpAnalysisResultDTO> analysisViewInfo(List<String> viewNameList, Map<String, String> fileMap) {
      Map<String, LcdpAnalysisResultDTO> viewResult = new HashMap<>();
      viewNameList.forEach(viewName -> {
         LcdpAnalysisResultDTO lcdpAnalysisResultDTO = new LcdpAnalysisResultDTO();
         lcdpAnalysisResultDTO.setEnable(true);
         lcdpAnalysisResultDTO.setAnalysisResultList(CollectionUtils.emptyList());
         LcdpViewBean view = JsonUtils.parse(fileMap.get(viewName), LcdpViewBean.class);

         try {
            this.lcdpDatabaseService.testSelectStatement(view.getSelectStatement());
         } catch (CheckedException var7) {
            lcdpAnalysisResultDTO.setEnable(false);
            lcdpAnalysisResultDTO.getAnalysisResultList().add(var7.getMessage());
         }

         viewResult.put(viewName, lcdpAnalysisResultDTO);
      });
      return viewResult;
   }

   @Override
   public LcdpViewBean selectPhysicalViewInfoList(String viewName) {
      return this.lcdpDatabaseService.selectPhysicalViewInfo(viewName);
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Transactional
   @ValidateDataWith(LcdpViewSaveDataValidator.class)
   public void save(Long id, RestJsonWrapperBean wrapper) {
      LcdpViewService.super.save(id, wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @ValidateDataWith(LcdpViewDeleteDataValidator.class)
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      LcdpViewBean toDeleteView = wrapper.parseUnique(this.getDao().getType());
      LcdpViewBean queryView = this.selectFirstByFilter(
         SearchFilter.instance().match("VIEWNAME", toDeleteView.getViewName()).filter(MatchPattern.EQ).match("SUBMITFLAG", "yes").filter(MatchPattern.EQ),
         new Order[0]
      );
      List<LcdpResourceLockBean> LockList = this.lcdpResourceLockService
         .selectListByFilter(
            SearchFilter.instance()
               .match("RESOURCEID", toDeleteView.getViewName())
               .filter(MatchPattern.SEQ)
               .match("LOCKUSERID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.SEQ),
            new Order[0]
         );
      if (LockList.isEmpty()) {
         throw new CheckedException("LCDP.MODULE.RESOURCES.TIP.UNCHECKOUT_CANNOT_DELETE");
      } else {
         List<Long> tableIdList = this.getDao().selectIdList(toDeleteView, ArrayUtils.asList("VIEWNAME"), new Order[0]);
         this.getDao().deleteByIdList(tableIdList);
         LcdpResourceVersionBean lcdpResourceVersionBean = new LcdpResourceVersionBean();
         lcdpResourceVersionBean.setResourceId(toDeleteView.getViewName());
         this.lcdpResourceVersionService.getDao().deleteBy(lcdpResourceVersionBean, "RESOURCEID");
         List<LcdpResourceVersionBean> versionList = this.lcdpResourceVersionService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", toDeleteView.getViewName()).filter(MatchPattern.OR), new Order[0]);
         versionList.forEach(version -> version.setResourceDeleteFlag("1"));
         this.lcdpResourceVersionService.getDao().update(versionList, "RESOURCEDELETEFLAG");
         this.lcdpResourceLockService.unLock(ArrayUtils.asList(toDeleteView.getViewName()));
         RedisHelper.evict("TC", toDeleteView.getViewName());
         RedisHelper.evict("EC", toDeleteView.getViewName());
         if (this.lcdpDatabaseService.isExistPhysicalView(toDeleteView.getViewName())) {
            this.lcdpDatabaseService.dropPhysicalView(toDeleteView);
         }

         if (queryView != null) {
            LcdpSubmitLogBean submitLog = new LcdpSubmitLogBean();
            submitLog.setId(ApplicationContextHelper.getNextIdentity());
            submitLog.setCommit(I18nHelper.getMessage("LCDP.RESOURCE.DELETED"));
            this.submitLogService.getDao().insert(submitLog);
            LcdpResourceVersionBean resourceVersion = new LcdpResourceVersionBean();
            resourceVersion.setId(ApplicationContextHelper.getNextIdentity());
            resourceVersion.setLogId(submitLog.getId());
            resourceVersion.setResourcePath(queryView.getViewName());
            resourceVersion.setResourceDeleteFlag("1");
            resourceVersion.setResourceAction("delete");
            resourceVersion.setResourceCategory("db-view");
            this.lcdpResourceVersionService.getDao().insert(resourceVersion);
         }

         this.resourceCheckoutRecordService.unCheckoutTableOrView(ArrayUtils.asList(toDeleteView.getViewName()));
      }
   }
}
