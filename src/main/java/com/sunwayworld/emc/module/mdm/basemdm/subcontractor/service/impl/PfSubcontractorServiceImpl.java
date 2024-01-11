package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.impl;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractorBean;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author licm@sunwayworld.com 2021年10月08日
 */
@Repository
@GikamBean
public class PfSubcontractorServiceImpl extends EmcSubcontractorServiceImpl {

	
	
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSubcontractorBean emcSubcontractor = jsonWrapper.parseUnique(EmcSubcontractorBean.class);
        emcSubcontractor.setId(ApplicationContextHelper.getNextIdentity());
        //赋值所属单位信息
        CoreUserBean user = new CoreUserBean();
        user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        emcSubcontractor.setExt$Item("orgid", parentOrgId);
        getDao().insert(emcSubcontractor);
        return emcSubcontractor.getId();
    }

}