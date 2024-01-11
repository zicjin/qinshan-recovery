package com.sunwayworld.emc.module.mdm.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityFoldersBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityFoldersDao;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityFoldersService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityFoldersServiceImpl implements CrmOpportunityFoldersService {

    @Autowired
    private CrmOpportunityFoldersDao crmOpportunityFoldersDao;

    @Override
    @SuppressWarnings("unchecked")
    public CrmOpportunityFoldersDao getDao() {
        return crmOpportunityFoldersDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapperBean) {
        CrmOpportunityFoldersBean crmOpportunityFoldersBean = wrapperBean.parseUnique(CrmOpportunityFoldersBean.class);
        String id = ApplicationContextHelper.getNextIdentity() + "";
        crmOpportunityFoldersBean.setId(id);
        getDao().insert(crmOpportunityFoldersBean);
        return id;
    }

}
