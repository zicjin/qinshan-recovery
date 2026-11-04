package com.sunwayworld.framework.support.suspendable.service;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.service.GenericDownloadableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Suspendable;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

public interface GenericSuspendableService<T extends Suspendable<ID>, ID extends Serializable> extends GenericService<T, ID>, GenericDownloadableService {
   @Transactional
   @AuditTrailEntry(AuditTrailType.SUSPEND)
   @Audit("AUDIT.SUSPEND")
   default void suspend(RestJsonWrapperBean wrapper) {
      this.doSuspendAction(wrapper, true);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UNSUSPEND)
   @Audit("AUDIT.UNSUSPEND")
   default void unsuspend(RestJsonWrapperBean wrapper) {
      this.doSuspendAction(wrapper, false);
   }

   default Page<T> selectSuspendablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      if (AbstractAuditable.class.isAssignableFrom(this.getDao().getType())) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DONE);
      }

      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(parameter, rowBounds);
   }

   default void setSuspendProperties(T proxy, boolean suspend) {
      if (!ClassUtils.isCglibProxyClass(proxy.getClass())) {
         throw new UnexpectedException("Set suspend properties expects proxy item.");
      } else {
         proxy.setLastSuspendedFlag(suspend ? "1" : "0");
         if (suspend) {
            proxy.setLastSuspendedById(LocalContextHelper.getLoginUserId());
            proxy.setLastSuspendedByName(LocalContextHelper.getLoginUser().getUserName());
            proxy.setLastSuspendedTime(LocalDateTime.now());
         } else {
            proxy.setLastSuspendedById(null);
            proxy.setLastSuspendedByName(null);
            proxy.setLastSuspendedTime(null);
         }
      }
   }

   default void doSuspendAction(RestJsonWrapperBean wrapper, boolean suspend) {
      List<T> itemList = wrapper.parse(this.getDao().getType());
      if (!itemList.isEmpty()) {
         List<T> proxyItemList = itemList.stream().map(i -> {
            T proxy = BeanUtils.getPropertyListenerProxy(this.getDao().getType());
            proxy.setId(i.getId());
            this.setSuspendProperties(proxy, suspend);
            return proxy;
         }).collect(Collectors.toList());
         this.<GenericDao<T, ID>>getDao().update(proxyItemList);
      }
   }
}
