package com.sunwayworld.module.item.helpdocument.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentCatalogBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentCatalogDTO;
import com.sunwayworld.module.item.helpdocument.dao.CoreHelpDocumentCatalogDao;
import com.sunwayworld.module.item.helpdocument.service.CoreHelpDocumentCatalogService;
import com.sunwayworld.module.item.helpdocument.service.CoreHelpDocumentService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreHelpDocumentCatalogServiceImpl implements CoreHelpDocumentCatalogService {
   @Autowired
   private CoreHelpDocumentCatalogDao coreHelpDocumentCatalogDao;
   @Autowired
   private CoreHelpDocumentService coreHelpDocumentService;

   public CoreHelpDocumentCatalogDao getDao() {
      return this.coreHelpDocumentCatalogDao;
   }

   @Cacheable(
      value = {"T_CORE_HELP_DOCUMENT_CATALOG"},
      key = "'ALL'"
   )
   @Override
   public List<CoreHelpDocumentCatalogBean> selectAll() {
      return CoreHelpDocumentCatalogService.super.selectAll();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      MapperParameter parameter = jsonWrapper.extractMapFilter();
      CoreHelpDocumentCatalogBean coreHelpDocumentCatalog = jsonWrapper.parseUnique(CoreHelpDocumentCatalogBean.class);
      Long orderNo = 1L;
      if (!ObjectUtils.isEmpty(coreHelpDocumentCatalog.getParentId())) {
         parameter.put("parentId", coreHelpDocumentCatalog.getParentId());
      } else {
         parameter.put("parentId", "root");
      }

      List<CoreHelpDocumentCatalogBean> coreHelpDocumentCatalogList = this.selectList(parameter);
      if (!CollectionUtils.isEmpty(coreHelpDocumentCatalogList)) {
         orderNo = coreHelpDocumentCatalogList.stream().mapToLong(e -> NumberUtils.parseLong(e.getOrderNo())).max().getAsLong() + 1L;
      }

      coreHelpDocumentCatalog.setId(ApplicationContextHelper.getNextIdentity());
      coreHelpDocumentCatalog.setOrderNo(orderNo);
      this.getDao().insert(coreHelpDocumentCatalog);
      return coreHelpDocumentCatalog.getId();
   }

   @Override
   public Page<CoreHelpDocumentBean> selectDocumentPaginationByHelpDocumentCatalogId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreHelpDocumentService.selectPaginationByFilter(SearchFilter.instance().match("HELPDOCUMENTCATALOGID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertDocument(RestJsonWrapperBean wrapper) {
      return this.coreHelpDocumentService.insert(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteDocument(RestJsonWrapperBean wrapper) {
      this.coreHelpDocumentService.delete(wrapper);
   }

   @Override
   public List<CoreHelpDocumentCatalogDTO> selectEditTree(String parentId, RestJsonWrapperBean wrapper) {
      List<CoreHelpDocumentCatalogBean> catalogList = this.selectAll();
      TreeDescriptor<CoreHelpDocumentCatalogBean> descriptor = new TreeDescriptor<>("id", "parentId", "helpDocumentCatalog", "orderNo");
      List<CoreHelpDocumentCatalogDTO> catalogDtoList = TreeHelper.parseTreeNode(catalogList, descriptor, CoreHelpDocumentCatalogDTO.class);
      TreeHelper.updateChildQty(catalogDtoList);
      return catalogDtoList;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void drag(RestJsonWrapperBean wrapper) {
      String currentCatalogId = wrapper.getParamValue("currentCatalogId");
      String dropParentCatalogId = wrapper.getParamValue("dropParentCatalogId");
      String dropPreviousCatalogId = wrapper.getParamValue("dropPreviousCatalogId");
      CoreHelpDocumentCatalogBean currentCatalog = this.getDao().selectById(Long.parseLong(currentCatalogId));
      currentCatalog.setParentId(StringUtils.isBlank(dropParentCatalogId) ? null : Long.parseLong(dropParentCatalogId));
      List<CoreHelpDocumentCatalogBean> updateCatalogList = new ArrayList<>();
      List<CoreHelpDocumentCatalogBean> dropCatalogList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", dropParentCatalogId).filter(MatchPattern.EQ), new Order[0]
      );
      if (StringUtils.isBlank(dropPreviousCatalogId)) {
         currentCatalog.setOrderNo(1L);
      } else {
         CoreHelpDocumentCatalogBean previousCatalog = this.getDao().selectById(Long.parseLong(dropPreviousCatalogId));
         currentCatalog.setOrderNo(previousCatalog.getOrderNo() + 1L);
      }

      dropCatalogList.forEach(e -> {
         Long currentOrderNo = e.getOrderNo();
         if (currentOrderNo >= currentCatalog.getOrderNo()) {
            e.setOrderNo(currentOrderNo + 1L);
            updateCatalogList.add(e);
         }
      });
      this.getDao().update(currentCatalog, new String[]{"PARENTID"});
      this.getDao().update(updateCatalogList, new String[]{"PARENTID", "ORDERNO"});
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.ACTIVATE)
   @Audit("AUDIT.ACTIVATE")
   @Override
   public void activate(RestJsonWrapperBean wrapper) {
      this.coreHelpDocumentService.activate(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DEACTIVATE)
   @Audit("AUDIT.DEACTIVATE")
   @Override
   public void deactivate(RestJsonWrapperBean wrapper) {
      this.coreHelpDocumentService.deactivate(wrapper);
   }

   @Override
   public Long getId() {
      return ApplicationContextHelper.getNextIdentity();
   }
}
