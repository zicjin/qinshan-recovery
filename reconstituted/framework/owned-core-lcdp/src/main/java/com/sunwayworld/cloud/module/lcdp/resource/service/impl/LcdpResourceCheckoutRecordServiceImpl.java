package com.sunwayworld.cloud.module.lcdp.resource.service.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpResourceCheckoutRecordDao;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
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
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpResourceCheckoutRecordServiceImpl implements LcdpResourceCheckoutRecordService {
   @Autowired
   private LcdpResourceCheckoutRecordDao lcdpResourceCheckoutRecordDao;
   @Autowired
   @Lazy
   private LcdpResourceService resourceService;
   @Autowired
   @Lazy
   private LcdpResourceLockService resourceLockService;
   @Autowired
   @Lazy
   private LcdpTableService tableService;
   @Autowired
   @Lazy
   private LcdpViewService viewService;
   @Autowired
   private CoreConstantService coreConstantService;
   @Autowired
   private CoreUserService coreUserService;

   public LcdpResourceCheckoutRecordDao getDao() {
      return this.lcdpResourceCheckoutRecordDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void checkoutResource(List<LcdpResourceBean> resourceList) {
      if (!resourceList.isEmpty()) {
         List<LcdpResourceCheckoutRecordBean> exitCategoryModuleList = this.selectListByFilter(
            SearchFilter.instance()
               .match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.EQ)
               .match("resourceCategory", LcdpConstant.CATEGORY_MODULE_CATEGORY_LIST)
               .filter(MatchPattern.OR),
            new Order[0]
         );
         List<Long> moduleIdList = resourceList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
         List<LcdpResourceCheckoutRecordBean> checkoutModuleList = exitCategoryModuleList.stream()
            .filter(rc -> "module".equals(rc.getResourceCategory()))
            .collect(Collectors.toList());
         List<Long> exitsModuleIdList = checkoutModuleList.stream().map(LcdpResourceCheckoutRecordBean::getResourceId).collect(Collectors.toList());
         List<LcdpResourceCheckoutRecordBean> checkoutCategoryList = exitCategoryModuleList.stream()
            .filter(rc -> "category".equals(rc.getResourceCategory()))
            .collect(Collectors.toList());
         List<Long> exitsCategoryIdList = checkoutCategoryList.stream().map(LcdpResourceCheckoutRecordBean::getResourceId).collect(Collectors.toList());
         moduleIdList.removeAll(exitsModuleIdList);
         List<LcdpResourceBean> moduleList = this.resourceService.selectListByIds(moduleIdList);
         List<Long> categoryIdList = moduleList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
         categoryIdList.removeAll(exitsCategoryIdList);
         List<LcdpResourceBean> categoryList = this.resourceService.selectListByIds(categoryIdList);
         resourceList.addAll(moduleList);
         resourceList.addAll(categoryList);
         List<LcdpResourceCheckoutRecordBean> resourceCheckoutRecordList = resourceList.stream().map(resource -> {
            LcdpResourceCheckoutRecordBean resourceCheckoutRecord = new LcdpResourceCheckoutRecordBean();
            BeanUtils.copyProperties(resource, resourceCheckoutRecord);
            resourceCheckoutRecord.setId(ApplicationContextHelper.getNextIdentity());
            resourceCheckoutRecord.setResourceId(resource.getId());
            resourceCheckoutRecord.setCheckoutTime(LocalDateTime.now());
            resourceCheckoutRecord.setCheckoutUserName(LocalContextHelper.getLoginUserName());
            resourceCheckoutRecord.setCheckoutUserId(LocalContextHelper.getLoginUserId());
            return resourceCheckoutRecord;
         }).collect(Collectors.toList());
         this.getDao().insert(resourceCheckoutRecordList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void unCheckoutResource(List<LcdpResourceBean> resourceList) {
      LcdpResourceBean resource = resourceList.stream().findFirst().get();
      String resourceCategory = resource.getResourceCategory();
      switch (resourceCategory) {
         case "category":
            this.dealCategory(resourceList);
            break;
         case "module":
            this.dealModule(resourceList);
            break;
         default:
            this.dealResource(resourceList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void checkoutTableOrView(String name, String desc, String category) {
      LcdpResourceCheckoutRecordBean resourceCheckoutRecord = new LcdpResourceCheckoutRecordBean();
      resourceCheckoutRecord.setId(ApplicationContextHelper.getNextIdentity());
      resourceCheckoutRecord.setResourceCategory(category);
      resourceCheckoutRecord.setTableName(name);
      resourceCheckoutRecord.setResourceDesc(desc);
      resourceCheckoutRecord.setCheckoutUserId(LocalContextHelper.getLoginUserId());
      resourceCheckoutRecord.setCheckoutUserName(LocalContextHelper.getLoginUserName());
      resourceCheckoutRecord.setCheckoutTime(LocalDateTime.now());
      this.getDao().insert(resourceCheckoutRecord);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void unCheckoutTableOrView(List<String> nameList) {
      List<LcdpResourceCheckoutRecordBean> checkoutRecordList = this.selectListByFilter(
         SearchFilter.instance().match("tableName", nameList).filter(MatchPattern.OR), new Order[0]
      );
      List<Long> deleteIdList = checkoutRecordList.stream().map(LcdpResourceCheckoutRecordBean::getId).collect(Collectors.toList());
      this.getDao().deleteByIdList(deleteIdList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void dealResourceCheckoutRecord() {
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_RS_CHECKOUT");
      if (queryCoreConstant == null) {
         List<LcdpResourceCheckoutRecordBean> allCheckoutRecordList = new ArrayList<>();
         List<LcdpResourceLockBean> lockList = this.resourceLockService
            .selectListByFilter(SearchFilter.instance().match("lockUserId", null).filter(MatchPattern.DIFFER), new Order[0]);
         List<String> checkUserIdList = lockList.stream().map(LcdpResourceLockBean::getLockUserId).collect(Collectors.toList());
         checkUserIdList = checkUserIdList.stream().distinct().collect(Collectors.toList());
         List<CoreUserBean> coreUserList = this.coreUserService.selectListByIds(checkUserIdList);
         Map<String, String> userId2UserNameMap = coreUserList.stream().collect(Collectors.toMap(CoreUserBean::getId, CoreUserBean::getUserName));
         this.dealCheckoutResouce(allCheckoutRecordList, lockList, userId2UserNameMap);
         List<LcdpResourceLockBean> checkoutTableList = lockList.stream()
            .filter(lock -> "table".equals(lock.getResourceCategory()))
            .collect(Collectors.toList());
         List<String> tableNameList = checkoutTableList.stream().map(LcdpResourceLockBean::getResourceId).collect(Collectors.toList());
         List<LcdpTableBean> tableList = this.tableService
            .selectListByFilter(
               SearchFilter.instance().match("tableName", tableNameList).filter(MatchPattern.OR).match("submitFlag", "no").filter(MatchPattern.EQ),
               new Order[0]
            );
         tableList.forEach(table -> {
            LcdpResourceCheckoutRecordBean resourceCheckoutRecord = new LcdpResourceCheckoutRecordBean();
            resourceCheckoutRecord.setId(ApplicationContextHelper.getNextIdentity());
            resourceCheckoutRecord.setTableName(table.getTableName());
            resourceCheckoutRecord.setTableDesc(table.getTableDesc());
            resourceCheckoutRecord.setResourceCategory("table");
            resourceCheckoutRecord.setCheckoutTime(table.getCreatedTime());
            resourceCheckoutRecord.setCheckoutUserName(table.getCreatedByName());
            resourceCheckoutRecord.setCheckoutUserId(table.getCreatedById());
            allCheckoutRecordList.add(resourceCheckoutRecord);
         });
         List<LcdpResourceLockBean> checkoutDBViewList = lockList.stream()
            .filter(lock -> "db-view".equals(lock.getResourceCategory()))
            .collect(Collectors.toList());
         List<String> viewNameList = checkoutDBViewList.stream().map(LcdpResourceLockBean::getResourceId).collect(Collectors.toList());
         List<LcdpViewBean> viewList = this.viewService
            .selectListByFilter(
               SearchFilter.instance().match("viewName", viewNameList).filter(MatchPattern.OR).match("submitFlag", "no").filter(MatchPattern.EQ), new Order[0]
            );
         viewList.forEach(view -> {
            LcdpResourceCheckoutRecordBean resourceCheckoutRecord = new LcdpResourceCheckoutRecordBean();
            resourceCheckoutRecord.setId(ApplicationContextHelper.getNextIdentity());
            resourceCheckoutRecord.setTableName(view.getViewName());
            resourceCheckoutRecord.setTableDesc(view.getViewName());
            resourceCheckoutRecord.setResourceCategory("db-view");
            resourceCheckoutRecord.setCheckoutTime(view.getCreatedTime());
            resourceCheckoutRecord.setCheckoutUserName(view.getCreatedByName());
            resourceCheckoutRecord.setCheckoutUserId(view.getCreatedById());
            allCheckoutRecordList.add(resourceCheckoutRecord);
         });
         this.getDao().insert(allCheckoutRecordList);
         CoreConstantBean coreConstant = new CoreConstantBean();
         coreConstant.setId("LCDP_RS_CHECKOUT");
         coreConstant.setConstantValue("1");
         coreConstant.setConstantDesc("低代码平台检出数据处理");
         coreConstant.setClassfication("系统");
         this.coreConstantService.getDao().insert(coreConstant);
      }
   }

   private void dealCheckoutResouce(
      List<LcdpResourceCheckoutRecordBean> allCheckoutRecordList, List<LcdpResourceLockBean> lockList, Map<String, String> userId2UserNameMap
   ) {
      List<LcdpResourceLockBean> pageAndScriptLockTempList = lockList.stream()
         .filter(lock -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(lock.getResourceCategory()))
         .collect(Collectors.toList());
      List<Long> pageAndScripIdList = pageAndScriptLockTempList.stream()
         .map(LcdpResourceLockBean::getResourceId)
         .map(Long::valueOf)
         .collect(Collectors.toList());
      List<LcdpResourceBean> pageAndScriptList = this.resourceService
         .selectListByFilter(
            SearchFilter.instance().match("id", pageAndScripIdList).filter(MatchPattern.OR).match("deleteFlag", "0").filter(MatchPattern.EQ), new Order[0]
         );
      if (!pageAndScriptList.isEmpty()) {
         List<Long> resourceIdList = pageAndScriptList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<Long> moduleIdList = pageAndScriptList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
         List<LcdpResourceBean> moduleList = this.resourceService.selectListByIds(moduleIdList);
         List<Long> categoryIdList = moduleList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
         List<LcdpResourceBean> categoryList = this.resourceService.selectListByIds(categoryIdList);
         Map<String, List<LcdpResourceLockBean>> checkUser2LockListMap = pageAndScriptLockTempList.stream()
            .filter(lock -> resourceIdList.contains(Long.valueOf(lock.getResourceId())))
            .collect(Collectors.groupingBy(LcdpResourceLockBean::getLockUserId));
         checkUser2LockListMap.forEach(
            (checkUser, LockList) -> {
               List<LcdpResourceBean> checkUserCheckoutList = new ArrayList<>();
               List<String> checkUserCheckoutIdList = LockList.stream().map(LcdpResourceLockBean::getResourceId).collect(Collectors.toList());
               List<LcdpResourceBean> checkUserCheckResourceList = pageAndScriptList.stream()
                  .filter(r -> checkUserCheckoutIdList.contains(r.getId().toString()))
                  .collect(Collectors.toList());
               checkUserCheckoutList.addAll(checkUserCheckResourceList);
               List<Long> checkUserCheckModuleIdList = checkUserCheckResourceList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
               List<LcdpResourceBean> checkUserCheckModuleList = moduleList.stream()
                  .filter(m -> checkUserCheckModuleIdList.contains(m.getId()))
                  .collect(Collectors.toList());
               checkUserCheckModuleList = checkUserCheckModuleList.stream()
                  .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(a -> a.getId()))), ArrayList::new));
               checkUserCheckoutList.addAll(checkUserCheckModuleList);
               List<Long> checkUserCheckCategoryIdList = checkUserCheckModuleList.stream().map(LcdpResourceBean::getParentId).collect(Collectors.toList());
               List<LcdpResourceBean> checkUserCheckCategoryList = categoryList.stream()
                  .filter(m -> checkUserCheckCategoryIdList.contains(m.getId()))
                  .collect(Collectors.toList());
               checkUserCheckCategoryList = checkUserCheckCategoryList.stream()
                  .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(a -> a.getId()))), ArrayList::new));
               checkUserCheckoutList.addAll(checkUserCheckCategoryList);
               List<LcdpResourceCheckoutRecordBean> checkoutRecordList = checkUserCheckoutList.stream().map(resource -> {
                  LcdpResourceCheckoutRecordBean resourceCheckoutRecord = new LcdpResourceCheckoutRecordBean();
                  resourceCheckoutRecord.setId(ApplicationContextHelper.getNextIdentity());
                  resourceCheckoutRecord.setResourceId(resource.getId());
                  resourceCheckoutRecord.setResourceDesc(resource.getResourceDesc());
                  resourceCheckoutRecord.setResourceName(resource.getResourceName());
                  resourceCheckoutRecord.setResourceCategory(resource.getResourceCategory());
                  resourceCheckoutRecord.setCheckoutUserId(checkUser);
                  resourceCheckoutRecord.setCheckoutUserName(userId2UserNameMap.get(checkUser));
                  resourceCheckoutRecord.setParentId(resource.getParentId());
                  return resourceCheckoutRecord;
               }).collect(Collectors.toList());
               allCheckoutRecordList.addAll(checkoutRecordList);
            }
         );
      }
   }

   private void dealResource(List<LcdpResourceBean> resourceList) {
      GikamConcurrentLocker.isolated("LCDP_RESOURCE_CHECKOUT_RECORD" + EncryptUtils.MD5Encrypt(resourceList.toString()));
      List<Long> resourceIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      if (StringUtils.equals(LocalContextHelper.getLoginUser().getUserType(), "sysAdmin")) {
         this.adminDealChannel(resourceIdList);
      } else {
         List<LcdpResourceCheckoutRecordBean> exitPageAndScriptList = this.selectListByFilter(
            SearchFilter.instance()
               .match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.EQ)
               .match("resourceId", resourceIdList)
               .filter(MatchPattern.OR),
            new Order[0]
         );
         if (!exitPageAndScriptList.isEmpty()) {
            List<Long> deleteIdList = exitPageAndScriptList.stream().map(LcdpResourceCheckoutRecordBean::getId).collect(Collectors.toList());
            List<LcdpResourceCheckoutRecordBean> exitCategoryModuleList = this.selectListByFilter(
               SearchFilter.instance()
                  .match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId())
                  .filter(MatchPattern.EQ)
                  .match("resourceCategory", LcdpConstant.CATEGORY_MODULE_CATEGORY_LIST)
                  .filter(MatchPattern.OR),
               new Order[0]
            );
            List<Long> moduleIdList = exitPageAndScriptList.stream().map(LcdpResourceCheckoutRecordBean::getParentId).collect(Collectors.toList());
            List<LcdpResourceCheckoutRecordBean> exitAllCurrentUserCheckoutPageAndScriptList = this.selectListByFilter(
               SearchFilter.instance()
                  .match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId())
                  .filter(MatchPattern.EQ)
                  .match("PARENTID", moduleIdList)
                  .filter(MatchPattern.OR),
               new Order[0]
            );
            Map<Long, List<LcdpResourceCheckoutRecordBean>> moduleId2PageAndScriptListMap = exitAllCurrentUserCheckoutPageAndScriptList.stream()
               .collect(Collectors.groupingBy(LcdpResourceCheckoutRecordBean::getParentId));
            List<Long> toDeleteCMResourceIdList = new ArrayList<>();
            moduleId2PageAndScriptListMap.forEach(
               (moduleId, pageAndScriptList) -> {
                  List<LcdpResourceCheckoutRecordBean> stayPageAndScriptList = pageAndScriptList.stream()
                     .filter(ps -> !resourceIdList.contains(ps.getResourceId()))
                     .collect(Collectors.toList());
                  moduleId2PageAndScriptListMap.put(moduleId, stayPageAndScriptList);
                  if (stayPageAndScriptList.isEmpty()) {
                     toDeleteCMResourceIdList.add(moduleId);
                  }
               }
            );
            if (!toDeleteCMResourceIdList.isEmpty()) {
               List<Long> categoryIdList = exitCategoryModuleList.stream()
                  .filter(cm -> toDeleteCMResourceIdList.contains(cm.getResourceId()))
                  .map(LcdpResourceCheckoutRecordBean::getParentId)
                  .collect(Collectors.toList());
               Map<Long, List<LcdpResourceCheckoutRecordBean>> categoryId2ModuleList = exitCategoryModuleList.stream()
                  .filter(cm -> categoryIdList.contains(cm.getParentId()))
                  .collect(Collectors.groupingBy(LcdpResourceCheckoutRecordBean::getParentId));
               categoryId2ModuleList.forEach(
                  (categoryId, moduleList) -> {
                     List<LcdpResourceCheckoutRecordBean> residueModuleList = moduleList.stream()
                        .filter(module -> !toDeleteCMResourceIdList.contains(module.getResourceId()))
                        .collect(Collectors.toList());
                     if (residueModuleList.isEmpty()) {
                        toDeleteCMResourceIdList.add(categoryId);
                     }
                  }
               );
            }

            deleteIdList.addAll(
               exitCategoryModuleList.stream()
                  .filter(cm -> toDeleteCMResourceIdList.contains(cm.getResourceId()))
                  .map(LcdpResourceCheckoutRecordBean::getId)
                  .collect(Collectors.toList())
            );
            this.getDao().deleteByIdList(deleteIdList);
         }
      }
   }

   private void dealModule(List<LcdpResourceBean> resourceList) {
      LcdpResourceBean resource = resourceList.stream().findFirst().get();
      List<LcdpResourceCheckoutRecordBean> exitCategoryModuleList = this.selectListByFilter(
         SearchFilter.instance()
            .match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId())
            .filter(MatchPattern.EQ)
            .match("resourceCategory", LcdpConstant.CATEGORY_MODULE_CATEGORY_LIST)
            .filter(MatchPattern.OR),
         new Order[0]
      );
      LcdpResourceCheckoutRecordBean module = exitCategoryModuleList.stream().filter(cm -> cm.getResourceId() == resource.getId()).findFirst().orElse(null);
      if (module != null) {
         Long moduleId = module.getResourceId();
         List<LcdpResourceCheckoutRecordBean> pageAndScriptList = this.selectListByFilter(
            SearchFilter.instance()
               .match("PARENTID", moduleId)
               .filter(MatchPattern.EQ)
               .match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.EQ),
            new Order[0]
         );
         List<LcdpResourceCheckoutRecordBean> moduleList = exitCategoryModuleList.stream()
            .filter(cm -> cm.getParentId() != null && cm.getParentId() == resource.getParentId())
            .collect(Collectors.toList());
         if (moduleList.size() == 1) {
            LcdpResourceCheckoutRecordBean category = exitCategoryModuleList.stream()
               .filter(cm -> cm.getResourceId() == resource.getParentId())
               .findFirst()
               .get();
            pageAndScriptList.add(category);
         }

         pageAndScriptList.add(module);
         List<Long> deleteIdList = pageAndScriptList.stream().map(LcdpResourceCheckoutRecordBean::getId).collect(Collectors.toList());
         this.getDao().deleteByIdList(deleteIdList);
      }
   }

   private void dealCategory(List<LcdpResourceBean> resourceList) {
      LcdpResourceBean resource = resourceList.stream().findFirst().get();
      List<LcdpResourceCheckoutRecordBean> exitCategoryModuleList = this.selectListByFilter(
         SearchFilter.instance()
            .match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId())
            .filter(MatchPattern.EQ)
            .match("resourceCategory", LcdpConstant.CATEGORY_MODULE_CATEGORY_LIST)
            .filter(MatchPattern.OR),
         new Order[0]
      );
      List<LcdpResourceCheckoutRecordBean> categoryList = exitCategoryModuleList.stream()
         .filter(cm -> cm.getResourceId() == resource.getId())
         .collect(Collectors.toList());
      if (!categoryList.isEmpty()) {
         List<LcdpResourceCheckoutRecordBean> moduleList = exitCategoryModuleList.stream()
            .filter(cm -> cm.getParentId() != null && cm.getParentId() == resource.getId())
            .collect(Collectors.toList());
         List<Long> moduleIdList = moduleList.stream().map(LcdpResourceCheckoutRecordBean::getId).collect(Collectors.toList());
         List<LcdpResourceCheckoutRecordBean> pageAndScriptList = this.selectListByFilter(
            SearchFilter.instance()
               .match("PARENTID", moduleIdList)
               .filter(MatchPattern.OR)
               .match("CHECKOUTUSERID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.EQ),
            new Order[0]
         );
         categoryList.addAll(moduleList);
         categoryList.addAll(pageAndScriptList);
         List<Long> deleteIdList = categoryList.stream().map(LcdpResourceCheckoutRecordBean::getId).collect(Collectors.toList());
         this.getDao().deleteByIdList(deleteIdList);
      }
   }

   private void adminDealChannel(List<Long> resourceIdList) {
      List<LcdpResourceCheckoutRecordBean> exitPageAndScriptList = this.selectListByFilter(
         SearchFilter.instance().match("resourceId", resourceIdList).filter(MatchPattern.OR), new Order[0]
      );
      if (!exitPageAndScriptList.isEmpty()) {
         List<Long> deleteIdList = exitPageAndScriptList.stream().map(LcdpResourceCheckoutRecordBean::getId).collect(Collectors.toList());
         List<String> checkUserIdList = exitPageAndScriptList.stream().map(LcdpResourceCheckoutRecordBean::getCheckoutUserId).collect(Collectors.toList());
         this.getDao().deleteByIdList(deleteIdList);
         List<Long> moduleIdList = exitPageAndScriptList.stream().map(LcdpResourceCheckoutRecordBean::getParentId).collect(Collectors.toList());
         List<LcdpResourceCheckoutRecordBean> exitModuleList = this.selectListByFilter(
            SearchFilter.instance().match("resourceId", moduleIdList).filter(MatchPattern.OR).match("checkoutUserId", checkUserIdList).filter(MatchPattern.OR),
            new Order[0]
         );
         Map<String, List<LcdpResourceCheckoutRecordBean>> checkoutUserId2ModuleListMap = exitModuleList.stream()
            .collect(Collectors.groupingBy(LcdpResourceCheckoutRecordBean::getCheckoutUserId));
         List<LcdpResourceCheckoutRecordBean> exitAllCurrentUserCheckoutPageAndScriptList = this.selectListByFilter(
            SearchFilter.instance().match("CHECKOUTUSERID", checkUserIdList).filter(MatchPattern.OR).match("PARENTID", moduleIdList).filter(MatchPattern.OR),
            new Order[0]
         );
         Map<String, List<LcdpResourceCheckoutRecordBean>> checkoutUserId2PageAndScriptListMap = exitAllCurrentUserCheckoutPageAndScriptList.stream()
            .collect(Collectors.groupingBy(LcdpResourceCheckoutRecordBean::getCheckoutUserId));
         List<Long> toDeleteModuleResourceIdList = new ArrayList<>();
         checkoutUserId2ModuleListMap.forEach(
            (checkoutUserId, checkoutModuleList) -> {
               List<LcdpResourceCheckoutRecordBean> pageAndScriptList = checkoutUserId2PageAndScriptListMap.get(checkoutUserId);
               if (ObjectUtils.isEmpty(pageAndScriptList)) {
                  List<Long> checkoutModuleIdList = checkoutModuleList.stream().map(LcdpResourceCheckoutRecordBean::getId).collect(Collectors.toList());
                  toDeleteModuleResourceIdList.addAll(checkoutModuleIdList);
               } else {
                  Map<Long, List<LcdpResourceCheckoutRecordBean>> moduleId2PageAndScriptListMap = pageAndScriptList.stream()
                     .collect(Collectors.groupingBy(LcdpResourceCheckoutRecordBean::getParentId));
                  checkoutModuleList.forEach(checkoutModule -> {
                     if (ObjectUtils.isEmpty(moduleId2PageAndScriptListMap.get(checkoutModule.getResourceId()))) {
                        toDeleteModuleResourceIdList.add(checkoutModule.getId());
                     }
                  });
               }
            }
         );
         this.getDao().deleteByIdList(toDeleteModuleResourceIdList);
         List<Long> exitCategoryIdList = exitModuleList.stream().map(LcdpResourceCheckoutRecordBean::getParentId).collect(Collectors.toList());
         List<LcdpResourceCheckoutRecordBean> exitCategoryList = this.selectListByFilter(
            SearchFilter.instance()
               .match("resourceId", exitCategoryIdList)
               .filter(MatchPattern.OR)
               .match("checkoutUserId", checkUserIdList)
               .filter(MatchPattern.OR),
            new Order[0]
         );
         Map<String, List<LcdpResourceCheckoutRecordBean>> checkoutUserId2CategoryListMap = exitCategoryList.stream()
            .collect(Collectors.groupingBy(LcdpResourceCheckoutRecordBean::getCheckoutUserId));
         List<LcdpResourceCheckoutRecordBean> exitAllCurrentUserCheckouModuleList = this.selectListByFilter(
            SearchFilter.instance()
               .match("CHECKOUTUSERID", checkUserIdList)
               .filter(MatchPattern.OR)
               .match("PARENTID", exitCategoryIdList)
               .filter(MatchPattern.OR),
            new Order[0]
         );
         Map<String, List<LcdpResourceCheckoutRecordBean>> checkoutUser2ModuleListMap = exitAllCurrentUserCheckouModuleList.stream()
            .collect(Collectors.groupingBy(LcdpResourceCheckoutRecordBean::getCheckoutUserId));
         List<Long> toDeleteCategoryResourceIdList = new ArrayList<>();
         checkoutUserId2CategoryListMap.forEach(
            (checkoutUserId, checkoutCategoryList) -> {
               List<LcdpResourceCheckoutRecordBean> moduleList = checkoutUser2ModuleListMap.get(checkoutUserId);
               if (ObjectUtils.isEmpty(moduleList)) {
                  List<Long> checkoutCategoryIdList = checkoutCategoryList.stream().map(LcdpResourceCheckoutRecordBean::getId).collect(Collectors.toList());
                  toDeleteCategoryResourceIdList.addAll(checkoutCategoryIdList);
               } else {
                  Map<Long, List<LcdpResourceCheckoutRecordBean>> categoryId2ModuleListMap = moduleList.stream()
                     .collect(Collectors.groupingBy(LcdpResourceCheckoutRecordBean::getParentId));
                  checkoutCategoryList.forEach(checkoutCategory -> {
                     if (ObjectUtils.isEmpty(categoryId2ModuleListMap.get(checkoutCategory.getResourceId()))) {
                        toDeleteCategoryResourceIdList.add(checkoutCategory.getId());
                     }
                  });
               }
            }
         );
         this.getDao().deleteByIdList(toDeleteCategoryResourceIdList);
      }
   }
}
