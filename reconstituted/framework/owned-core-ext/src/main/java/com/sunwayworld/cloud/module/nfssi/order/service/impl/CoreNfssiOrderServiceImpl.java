package com.sunwayworld.cloud.module.nfssi.order.service.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderQueryParamDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderTestItemBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonDTO;
import com.sunwayworld.cloud.module.nfssi.order.persistent.dao.CoreNfssiOrderDao;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiOrderService;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiOrderTestItemService;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiSystemService;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiTestReasonService;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiVerifyReasonService;
import com.sunwayworld.cloud.module.nfssi.order.support.NfssiConvertHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
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
public class CoreNfssiOrderServiceImpl implements CoreNfssiOrderService {
   @Autowired
   private CoreNfssiOrderDao coreNfssiOrderDao;
   @Autowired
   @Lazy
   private CoreNfssiOrderTestItemService coreNfssiOrderTestItemService;
   @Autowired
   @Lazy
   private CoreNfssiTestReasonService coreNfssiTestReasonService;
   @Autowired
   @Lazy
   private CoreNfssiVerifyReasonService coreNfssiVerifyReasonService;
   @Autowired
   @Lazy
   private CoreNfssiSystemService coreNfssiSystemOrderService;

   public CoreNfssiOrderDao getDao() {
      return this.coreNfssiOrderDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public String insert(RestJsonWrapperBean jsonWrapper) {
      CoreNfssiOrderQueryParamDTO orderQueryParam = jsonWrapper.parseUnique(CoreNfssiOrderQueryParamDTO.class);
      String token = this.coreNfssiSystemOrderService.getToken();
      orderQueryParam.setToken(token);
      List<String> orderIdList = this.coreNfssiSystemOrderService.getOrderList(orderQueryParam);
      return orderIdList != null && !orderIdList.isEmpty() ? this.insertUpdateOrderList(orderIdList, orderQueryParam.getSampleType()) : null;
   }

   @Override
   public Page<CoreNfssiOrderTestItemBean> selectOrderTestItemPaginationByNfssiOrderId(String id, RestJsonWrapperBean wrapper) {
      return this.coreNfssiOrderTestItemService.selectPaginationByFilter(SearchFilter.instance().match("ORDERID", id).filter(MatchPattern.SEQ), wrapper);
   }

   @Override
   public Page<CoreNfssiTestReasonBean> selectTestReasonPaginationByTestItemId(Long testItemId, RestJsonWrapperBean wrapper) {
      return this.coreNfssiTestReasonService.selectPaginationByFilter(SearchFilter.instance().match("TESTITEMID", testItemId).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public Page<CoreNfssiVerifyReasonBean> selectVerifyReasonPaginationByTestItemId(Long testItemId, RestJsonWrapperBean wrapper) {
      return this.coreNfssiVerifyReasonService
         .selectPaginationByFilter(SearchFilter.instance().match("TESTITEMID", testItemId).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void reRequest(RestJsonWrapperBean wrapper) {
      List<String> coreNfssiOrderIdList = wrapper.parseId(String.class);
      this.insertUpdateOrderList(coreNfssiOrderIdList, null);
   }

   protected String insertUpdateOrderList(List<String> idList, String sampleType) {
      List<CoreNfssiOrderBean> oriNfssiOrderList = this.selectListByIds(idList);
      List<CoreNfssiOrderBean> nfssiOrderList4InsertUpdate = new ArrayList<>();
      List<CoreNfssiOrderTestItemBean> nfssiOrderTestItemList4Insert = new ArrayList<>();
      List<CoreNfssiTestReasonBean> nfssiTestReasonList4Insert = new ArrayList<>();
      List<CoreNfssiVerifyReasonBean> nfssiVerifyReasonList4Insert = new ArrayList<>();
      idList.forEach(e -> {
         CoreNfssiOrderDTO nfssiOrderDTO = this.coreNfssiSystemOrderService.getOrderInfoById(e);
         CoreNfssiOrderBean nfssiOrder = new CoreNfssiOrderBean();
         NfssiConvertHelper.convert(nfssiOrderDTO, nfssiOrder);
         if (!StringUtils.isEmpty(sampleType)) {
            nfssiOrder.setSampleType(sampleType);
         } else if (StringUtils.isEmpty(nfssiOrder.getSampleType())) {
            CoreNfssiOrderBean oriSample = oriNfssiOrderList.stream().filter(f -> e.equals(f.getId())).findFirst().orElse(null);
            if (oriSample != null) {
               nfssiOrder.setSampleType(oriSample.getSampleType());
            }
         }

         nfssiOrderList4InsertUpdate.add(nfssiOrder);
         nfssiOrderDTO.getItemInfo().forEach(itemDTO -> {
            CoreNfssiOrderTestItemBean nfssiOrderTestItem = new CoreNfssiOrderTestItemBean();
            NfssiConvertHelper.convert(itemDTO, nfssiOrderTestItem);
            nfssiOrderTestItemList4Insert.add(nfssiOrderTestItem);
            List<CoreNfssiTestReasonDTO> nfssiTestReasonDTOList = itemDTO.getTestReason();
            if (!nfssiTestReasonDTOList.isEmpty()) {
               itemDTO.getTestReason().forEach(testReasonDTO -> {
                  CoreNfssiTestReasonBean nfssiTestReason = new CoreNfssiTestReasonBean();
                  NfssiConvertHelper.convert(testReasonDTO, nfssiTestReason);
                  nfssiTestReasonList4Insert.add(nfssiTestReason);
               });
            }

            List<CoreNfssiVerifyReasonDTO> nfssiVerifyReasonDTOList = itemDTO.getVerifyReason();
            if (!nfssiVerifyReasonDTOList.isEmpty()) {
               nfssiVerifyReasonDTOList.forEach(verifyReasonDTO -> {
                  CoreNfssiVerifyReasonBean nfssiVerifyReason = new CoreNfssiVerifyReasonBean();
                  NfssiConvertHelper.convert(verifyReasonDTO, nfssiVerifyReason);
                  nfssiVerifyReasonList4Insert.add(nfssiVerifyReason);
               });
            }
         });
      });
      if (nfssiOrderList4InsertUpdate.isEmpty()) {
         return null;
      } else {
         List<CoreNfssiOrderBean> allNfssiOrderList = this.selectAll();
         List<String> allIdList = allNfssiOrderList.stream().map(e -> e.getId()).collect(Collectors.toList());
         List<CoreNfssiOrderBean> updateNfssiOrderList = nfssiOrderList4InsertUpdate.stream()
            .filter(f -> allIdList.contains(f.getId()))
            .collect(Collectors.toList());
         List<CoreNfssiOrderBean> insertNfssiOrderList = nfssiOrderList4InsertUpdate.stream()
            .filter(f -> !allIdList.contains(f.getId()))
            .collect(Collectors.toList());
         this.getDao().updateIfChanged(updateNfssiOrderList);
         List<CoreNfssiOrderTestItemBean> deleteNfssiOrderTestItemList = this.coreNfssiOrderTestItemService
            .selectListByFilter(SearchFilter.instance().match("ORDERID", idList).filter(MatchPattern.OR), new Order[0]);
         List<Long> deleteOrderTestItemIdList = deleteNfssiOrderTestItemList.stream().map(e -> e.getId()).collect(Collectors.toList());
         if (!deleteOrderTestItemIdList.isEmpty()) {
            this.coreNfssiOrderTestItemService.getDao().deleteByIdList(deleteOrderTestItemIdList);
            List<CoreNfssiTestReasonBean> deleteTestReasonList = this.coreNfssiTestReasonService
               .selectListByFilter(SearchFilter.instance().match("TESTITEMID", deleteOrderTestItemIdList).filter(MatchPattern.OR), new Order[0]);
            List<CoreNfssiVerifyReasonBean> deleteVerifyReasonList = this.coreNfssiVerifyReasonService
               .selectListByFilter(SearchFilter.instance().match("TESTITEMID", deleteOrderTestItemIdList).filter(MatchPattern.OR), new Order[0]);
            this.coreNfssiTestReasonService
               .getDao()
               .deleteByIdList(deleteTestReasonList.stream().map(CoreNfssiTestReasonBean::getId).collect(Collectors.toList()));
            this.coreNfssiVerifyReasonService
               .getDao()
               .deleteByIdList(deleteVerifyReasonList.stream().map(CoreNfssiVerifyReasonBean::getId).collect(Collectors.toList()));
         }

         this.getDao().insert(insertNfssiOrderList);
         this.coreNfssiOrderTestItemService.getDao().insert(nfssiOrderTestItemList4Insert);
         this.coreNfssiTestReasonService.getDao().insert(nfssiTestReasonList4Insert);
         this.coreNfssiVerifyReasonService.getDao().insert(nfssiVerifyReasonList4Insert);
         return idList.get(0);
      }
   }
}
