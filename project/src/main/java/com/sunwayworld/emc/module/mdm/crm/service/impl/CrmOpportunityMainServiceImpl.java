package com.sunwayworld.emc.module.mdm.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityMainDao;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityMainService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityMainServiceImpl implements CrmOpportunityMainService {

    @Autowired
    private CrmOpportunityMainDao crmOpportunityMainDao;

    @Override
    @SuppressWarnings("unchecked")
    public CrmOpportunityMainDao getDao() {
        return crmOpportunityMainDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapperBean) {
        CrmOpportunityMainBean crmOpportunityMainBean = wrapperBean.parseUnique(CrmOpportunityMainBean.class);
        String id = ApplicationContextHelper.getNextIdentity() + "";
        crmOpportunityMainBean.setId(id);
        getDao().insert(crmOpportunityMainBean);
        return id;
    }

}
