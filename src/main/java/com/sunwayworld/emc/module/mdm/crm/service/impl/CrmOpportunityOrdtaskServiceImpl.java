package com.sunwayworld.emc.module.mdm.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityOrdtaskBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityOrdtaskDao;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOrdtaskService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityOrdtaskServiceImpl implements CrmOpportunityOrdtaskService {

    @Autowired
    private CrmOpportunityOrdtaskDao crmOpportunityOrdtaskDao;

    @Override
    @SuppressWarnings("unchecked")
    public CrmOpportunityOrdtaskDao getDao() {
        return crmOpportunityOrdtaskDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapperBean) {
        CrmOpportunityOrdtaskBean crmOpportunityOrdtaskBean = wrapperBean.parseUnique(CrmOpportunityOrdtaskBean.class);
        String id = ApplicationContextHelper.getNextIdentity() + "";
        crmOpportunityOrdtaskBean.setId(id);
        getDao().insert(crmOpportunityOrdtaskBean);
        return id;
    }

}
