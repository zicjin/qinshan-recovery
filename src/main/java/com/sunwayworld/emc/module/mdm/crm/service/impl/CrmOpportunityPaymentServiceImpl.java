package com.sunwayworld.emc.module.mdm.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityPaymentBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityPaymentDao;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityPaymentService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityPaymentServiceImpl implements CrmOpportunityPaymentService {

    @Autowired
    private CrmOpportunityPaymentDao crmOpportunityPaymentDao;

    @Override
    @SuppressWarnings("unchecked")
    public CrmOpportunityPaymentDao getDao() {
        return crmOpportunityPaymentDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapperBean) {
        CrmOpportunityPaymentBean crmOpportunityPaymentBean = wrapperBean.parseUnique(CrmOpportunityPaymentBean.class);
        String id = ApplicationContextHelper.getNextIdentity() + "";
        crmOpportunityPaymentBean.setId(id);
        getDao().insert(crmOpportunityPaymentBean);
        return id;
    }

}
