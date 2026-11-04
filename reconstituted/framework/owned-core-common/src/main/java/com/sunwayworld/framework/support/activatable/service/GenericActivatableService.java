package com.sunwayworld.framework.support.activatable.service;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Activatable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface GenericActivatableService<T extends Activatable<ID>, ID extends Serializable> extends GenericService<T, ID> {
   @Transactional
   @AuditTrailEntry(AuditTrailType.ACTIVATE)
   @Audit("AUDIT.ACTIVATE")
   default void activate(RestJsonWrapperBean wrapper) {
      Class<T> type = this.getDao().getType();
      List<T> itemList = wrapper.parse(type);
      itemList.forEach(i -> this.setActivateProperties((T)i, true));
      this.<GenericDao<T, ID>>getDao().update(itemList, "ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME");
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DEACTIVATE)
   @Audit("AUDIT.DEACTIVATE")
   default void deactivate(RestJsonWrapperBean wrapper) {
      Class<T> type = this.getDao().getType();
      List<T> itemList = wrapper.parse(type);
      itemList.forEach(i -> this.setActivateProperties((T)i, false));
      this.<GenericDao<T, ID>>getDao().update(itemList, "ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME");
   }

   default Page<T> selectActivatablePagination(RestJsonWrapperBean wrapper) {
      throw new UnsupportedException("You should implements this method if required.");
   }

   default void setActivateProperties(T proxy, boolean activate) {
      proxy.setActivatedFlag(activate ? "1" : "0");
      if (activate) {
         proxy.setActivatedById(LocalContextHelper.getLoginUserId());
         proxy.setActivatedByName(LocalContextHelper.getLoginUser().getUserName());
         proxy.setActivatedTime(LocalDateTime.now());
      } else {
         proxy.setActivatedById(null);
         proxy.setActivatedByName(null);
         proxy.setActivatedTime(null);
      }
   }
}
