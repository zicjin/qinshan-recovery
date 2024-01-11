package com.sunwayworld.emc.module.mdm.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityExpensesBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityExpensesDao;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityExpensesService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityExpensesServiceImpl implements CrmOpportunityExpensesService {

    @Autowired
    private CrmOpportunityExpensesDao crmOpportunityExpensesDao;

    @Override
    @SuppressWarnings("unchecked")
    public CrmOpportunityExpensesDao getDao() {
        return crmOpportunityExpensesDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapperBean) {
        CrmOpportunityExpensesBean crmOpportunityExpensesBean = wrapperBean.parseUnique(CrmOpportunityExpensesBean.class);
        String id = ApplicationContextHelper.getNextIdentity() + "";
        crmOpportunityExpensesBean.setId(id);
        getDao().insert(crmOpportunityExpensesBean);
        return id;
    }

}
