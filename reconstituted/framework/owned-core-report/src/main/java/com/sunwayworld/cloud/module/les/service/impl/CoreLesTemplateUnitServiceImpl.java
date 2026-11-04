package com.sunwayworld.cloud.module.les.service.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesTemplateUnitDao;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateUnitService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreLesTemplateUnitServiceImpl implements CoreLesTemplateUnitService {
   @Autowired
   private CoreLesTemplateUnitDao unitDao;

   public CoreLesTemplateUnitDao getDao() {
      return this.unitDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void dedentSection(Long unitId, RestJsonWrapperBean wrapper) {
      CoreLesTemplateUnitBean unit = this.getDao().selectById(unitId);
      if (unit.getParentId() != null) {
         Long parentId = unit.getParentId();
         CoreLesTemplateUnitBean parentUnit = this.getDao().selectById(unit.getParentId());
         unit.setParentId(parentUnit.getParentId());
         unit.setOrderNo(ApplicationContextHelper.getNextOrderNo());
         this.getDao().update(unit, new String[]{"PARENTID", "ORDERNO"});
         List<CoreLesTemplateUnitBean> itemList = wrapper.parse(CoreLesTemplateUnitBean.class);
         if (!itemList.isEmpty()) {
            List<CoreLesTemplateUnitBean> updatableUnitList = this.getDao()
               .selectListByIds(itemList.stream().map(e -> e.getId()).collect(Collectors.toList()), new Order[0]);
            List<CoreLesTemplateUnitBean> updateUnitList = updatableUnitList.stream()
               .filter(e -> !parentId.equals(e.getParentId()))
               .collect(Collectors.toList());
            updateUnitList.forEach(e -> e.setOrderNo(ApplicationContextHelper.getNextOrderNo()));
            this.getDao().update(updateUnitList, new String[]{"ORDERNO"});
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void indentSection(Long unitId, Long parentId, RestJsonWrapperBean wrapper) {
      CoreLesTemplateUnitBean unit = this.getDao().selectById(unitId);
      unit.setParentId(parentId);
      unit.setOrderNo(ApplicationContextHelper.getNextOrderNo());
      this.getDao().update(unit, new String[]{"PARENTID", "ORDERNO"});
      List<CoreLesTemplateUnitBean> itemList = wrapper.parse(CoreLesTemplateUnitBean.class);
      if (!itemList.isEmpty()) {
         itemList.forEach(e -> e.setOrderNo(ApplicationContextHelper.getNextOrderNo()));
         this.getDao().update(itemList, new String[]{"ORDERNO"});
      }
   }

   public void setProperty(CoreLesTemplateUnitBean lesTemplateUnit, boolean dataRepeatInsertFlag) {
      lesTemplateUnit.setExt$Item("oldid", lesTemplateUnit.getId().toString());
      lesTemplateUnit.setId(ApplicationContextHelper.getNextIdentity());
   }

   @Transactional
   @Override
   public void postImport(List<CoreLesTemplateUnitBean> lesTmplUnitList) {
      List<CoreLesTemplateUnitBean> lesTmplUnitList4Parent = lesTmplUnitList.stream().filter(u -> u.getParentId() != null).collect(Collectors.toList());
      if (!lesTmplUnitList4Parent.isEmpty()) {
         lesTmplUnitList4Parent.stream()
            .forEach(
               u -> {
                  CoreLesTemplateUnitBean filterParentUnit = lesTmplUnitList.stream()
                     .filter(up -> u.getParentId().toString().equals(up.getExt$Item("oldid")))
                     .findFirst()
                     .orElse(null);
                  if (filterParentUnit != null) {
                     u.setParentId(filterParentUnit.getId());
                  }
               }
            );
         this.getDao().update(lesTmplUnitList4Parent, new String[]{"PARENTID"});
      }
   }
}
