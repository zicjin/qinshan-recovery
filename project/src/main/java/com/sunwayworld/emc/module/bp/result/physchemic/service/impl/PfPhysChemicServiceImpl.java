package com.sunwayworld.emc.module.bp.result.physchemic.service.impl;



import java.time.LocalDateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.sunwayworld.emc.module.bp.result.physchemic.bean.PfPhysChemicBean;
import com.sunwayworld.emc.module.bp.result.physchemic.dao.PfPhysChemicDao;
import com.sunwayworld.emc.module.bp.result.physchemic.service.PfPhysChemicService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author licm@sunwayworld.com 2022年01月10日
 */
@Repository
@GikamBean
public class PfPhysChemicServiceImpl implements PfPhysChemicService {
    @Autowired
    private PfPhysChemicDao pfPhysChemicDao;


    @Override
    @SuppressWarnings("unchecked")
    public PfPhysChemicDao getDao() {
        return pfPhysChemicDao;
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        String runId = jsonWrapper.getParamMap().get("runId");

    	PfPhysChemicBean physChemicBean = new PfPhysChemicBean();
    	physChemicBean.setId(ApplicationContextHelper.getNextIdentity());
    	physChemicBean.setRunId(runId);
    	physChemicBean.setCreatedById(LocalContextHelper.getLoginUserId());
    	physChemicBean.setCreatedByName(LocalContextHelper.getLoginUserName());
    	physChemicBean.setCreatedTime(LocalDateTime.now());
    	physChemicBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
    	physChemicBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        getDao().insert(physChemicBean);
        return physChemicBean.getId();
    }
}