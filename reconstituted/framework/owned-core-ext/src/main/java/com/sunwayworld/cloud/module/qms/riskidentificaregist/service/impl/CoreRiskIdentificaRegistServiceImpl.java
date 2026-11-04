package com.sunwayworld.cloud.module.qms.riskidentificaregist.service.impl;

import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaFactorBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaRegistBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.persistent.dao.CoreRiskIdentificaRegistDao;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.service.CoreRiskIdentificaFactorService;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.service.CoreRiskIdentificaRegistService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.id.GikamIdHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreRiskIdentificaRegistServiceImpl implements CoreRiskIdentificaRegistService {
   @Autowired
   private CoreRiskIdentificaRegistDao coreRiskIdentificaRegistDao;
   @Autowired
   @Lazy
   private CoreRiskIdentificaFactorService coreRiskIdentificaFactorService;

   public CoreRiskIdentificaRegistDao getDao() {
      return this.coreRiskIdentificaRegistDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreRiskIdentificaRegistBean coreRiskIdentificaRegist = wrapper.parseUnique(CoreRiskIdentificaRegistBean.class);
      coreRiskIdentificaRegist.setId(GikamIdHelper.nextId());
      coreRiskIdentificaRegist.setRiskNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "FX"));
      this.getDao().insert(coreRiskIdentificaRegist);
      return coreRiskIdentificaRegist.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public List<Long> insertRiskIdentificaFactor(Long id, RestJsonWrapperBean wrapper) {
      List<CoreRiskIdentificaFactorBean> riskIdentificaFactorList = wrapper.parse(CoreRiskIdentificaFactorBean.class);
      List<CoreRiskIdentificaFactorBean> existedRiskIdentificaFactorList = this.coreRiskIdentificaFactorService
         .getDao()
         .selectListByOneColumnValue(id, "RISKREGISTID");
      List<Long> riskFactorIdList = existedRiskIdentificaFactorList.stream().map(e -> e.getRiskFactorId()).collect(Collectors.toList());
      if (!riskFactorIdList.isEmpty()) {
         riskIdentificaFactorList = riskIdentificaFactorList.stream().filter(e -> !riskFactorIdList.contains(e.getRiskFactorId())).collect(Collectors.toList());
      }

      if (riskIdentificaFactorList.isEmpty()) {
         return null;
      } else {
         List<Long> idList = ApplicationContextHelper.getNextIdentityList(riskIdentificaFactorList.size());

         for (int i = 0; i < riskIdentificaFactorList.size(); i++) {
            riskIdentificaFactorList.get(i).setRiskRegistId(id);
            riskIdentificaFactorList.get(i).setId(idList.get(i));
         }

         this.coreRiskIdentificaFactorService.getDao().insert(riskIdentificaFactorList);
         return idList;
      }
   }

   @Override
   public Page<CoreRiskIdentificaFactorBean> selectRiskIdentificaFactorPaginationByRegistId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreRiskIdentificaFactorService.selectPaginationByFilter(SearchFilter.instance().match("RISKREGISTID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteRiskIdentificaFactor(RestJsonWrapperBean wrapper) {
      this.coreRiskIdentificaFactorService.delete(wrapper);
   }
}
