package com.sunwayworld.cloud.module.lcdp.resourcelock.service.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.persistent.dao.LcdpResourceLockDao;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpResourceLockServiceImpl implements LcdpResourceLockService {
   @Autowired
   private LcdpResourceLockDao lcdpResourceLockDao;
   @Lazy
   @Autowired
   private LcdpResourceService lcdpResourceService;

   public LcdpResourceLockDao getDao() {
      return this.lcdpResourceLockDao;
   }

   @Override
   public RestValidationResultBean validateResourceEditable(List<String> resourceIdList) {
      if (resourceIdList.isEmpty()) {
         return new RestValidationResultBean(true);
      } else {
         List<LcdpResourceLockBean> resourceLockList = this.getDao().selectListByOneColumnValues(resourceIdList, "RESOURCEID", new Order[0]);
         if (resourceLockList.isEmpty()) {
            return new RestValidationResultBean(true);
         } else {
            List<LcdpResourceLockBean> lockList = new ArrayList<>();

            for (String resourceId : resourceIdList) {
               List<LcdpResourceLockBean> filterList = resourceLockList.stream()
                  .filter(f -> ObjectUtils.equals(resourceId, f.getResourceId()))
                  .collect(Collectors.toList());
               LcdpResourceLockBean resourceLockBean = filterList.get(0);
               if (!StringUtils.isEmpty(resourceLockBean.getLockUserId()) && !LocalContextHelper.getLoginUserId().equals(resourceLockBean.getLockUserId())) {
                  lockList.add(resourceLockBean);
               }
            }

            if (lockList.size() == 0) {
               return new RestValidationResultBean(true);
            } else {
               List<LcdpResourceBean> resourceList = this.lcdpResourceService
                  .selectListByIds(resourceIdList.stream().map(ex -> Long.parseLong(ex)).collect(Collectors.toList()));
               String message = "";

               for (LcdpResourceLockBean e : lockList) {
                  LcdpResourceBean resource = resourceList.stream()
                     .filter(f -> ObjectUtils.equals(Long.parseLong(e.getResourceId()), f.getId()))
                     .findFirst()
                     .orElse(new LcdpResourceBean());
                  message = message + "资源文件" + resource.getPath() + "正被" + e.getLockUserId() + "编辑,无法检出;\r\n";
               }

               return new RestValidationResultBean(false, message);
            }
         }
      }
   }

   @Override
   public RestValidationResultBean validateResourceEditable(String resourceId) {
      List<LcdpResourceLockBean> resourceLockList = this.selectListByFilter(
         SearchFilter.instance().match("RESOURCEID", resourceId).filter(MatchPattern.EQ), new Order[0]
      );
      if (resourceLockList.isEmpty()) {
         return new RestValidationResultBean(true);
      } else {
         LcdpResourceLockBean resourceLockBean = resourceLockList.get(0);
         if (StringUtils.isEmpty(resourceLockBean.getLockUserId())) {
            return new RestValidationResultBean(true);
         } else {
            return LocalContextHelper.getLoginUserId().equals(resourceLockBean.getLockUserId())
               ? new RestValidationResultBean(true)
               : new RestValidationResultBean(
                  false,
                  resourceLockBean.getLockUserId()
                     + I18nHelper.getMessage(String.format("LCDP.MODULE.RESOUCES.TIP.%s_EDITING", resourceLockBean.getResourceCategory().toUpperCase()))
               );
         }
      }
   }

   @Transactional
   @Override
   public void lock(String resourceId, String resourceCategory) {
      GikamConcurrentLocker.isolated("RESOURCE_CHECK_OUT$" + resourceId);
      List<LcdpResourceLockBean> lockList = this.selectListByFilter(
         SearchFilter.instance().match("RESOURCEID", resourceId).filter(MatchPattern.EQ), new Order[0]
      );
      List<LcdpResourceLockBean> lockLockList = lockList.stream().filter(lock -> !StringUtils.isEmpty(lock.getLockUserId())).collect(Collectors.toList());
      if (!lockLockList.isEmpty()) {
         throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.RESOURCES.TIP.CURRENT_RESOURCE_HAS_BEEN_CHECKOUT"));
      } else {
         List<LcdpResourceLockBean> resourceLockList = this.selectListByFilter(
            SearchFilter.instance().match("RESOURCEID", resourceId).filter(MatchPattern.EQ), new Order[0]
         );
         if (resourceLockList.isEmpty()) {
            LcdpResourceLockBean insertResourceLock = new LcdpResourceLockBean();
            insertResourceLock.setId(ApplicationContextHelper.getNextIdentity());
            insertResourceLock.setResourceId(resourceId);
            insertResourceLock.setResourceCategory(resourceCategory);
            insertResourceLock.setLockUserId(LocalContextHelper.getLoginUserId());
            this.getDao().insert(insertResourceLock);
         } else {
            LcdpResourceLockBean updateResourceLock = resourceLockList.get(0);
            updateResourceLock.setLockUserId(LocalContextHelper.getLoginUserId());
            this.getDao().update(updateResourceLock, new String[]{"LOCKUSERID"});
         }
      }
   }

   @Transactional
   @Override
   public void unLock(List<String> resourceIdList) {
      if (!resourceIdList.isEmpty()) {
         List<LcdpResourceLockBean> resourceLockList = this.selectListByFilter(
            SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR), new Order[0]
         );
         List<LcdpResourceLockBean> updateResourceLockList = resourceLockList.stream().map(lock -> {
            lock.setLockUserId(null);
            return (LcdpResourceLockBean)lock;
         }).collect(Collectors.toList());
         this.getDao().update(updateResourceLockList, new String[]{"LOCKUSERID"});
      }
   }

   @Transactional
   @Override
   public void lockResource(List<LcdpResourceBean> resourceList) {
      if (!ObjectUtils.isEmpty(resourceList)) {
         List<LcdpResourceLockBean> lockList = resourceList.stream().map(resource -> {
            LcdpResourceLockBean lock = new LcdpResourceLockBean();
            lock.setId(ApplicationContextHelper.getNextIdentity());
            lock.setLockUserId(LocalContextHelper.getLoginUserId());
            lock.setResourceId(resource.getId().toString());
            lock.setResourceCategory(resource.getResourceCategory());
            return lock;
         }).collect(Collectors.toList());
         this.getDao().insert(lockList);
      }
   }
}
