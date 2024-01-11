package com.sunwayworld.emc.module.mdm.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityOrdersBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityOrdersDao;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOrdersService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityOrdersServiceImpl implements CrmOpportunityOrdersService {

    @Autowired
    private CrmOpportunityOrdersDao crmOpportunityOrdersDao;

    @Override
    @SuppressWarnings("unchecked")
    public CrmOpportunityOrdersDao getDao() {
        return crmOpportunityOrdersDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapperBean) {
        CrmOpportunityOrdersBean crmOpportunityOrdersBean = wrapperBean.parseUnique(CrmOpportunityOrdersBean.class);
        String id = ApplicationContextHelper.getNextIdentity() + "";
        crmOpportunityOrdersBean.setId(id);
        getDao().insert(crmOpportunityOrdersBean);
        return id;
    }

}
