package com.sunwayworld.emc.module.mdm.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmCustomerMainBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmCustomerMainDao;
import com.sunwayworld.emc.module.mdm.crm.service.CrmCustomerMainService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmCustomerMainServiceImpl implements CrmCustomerMainService {

    @Autowired
    private CrmCustomerMainDao crmCustomerMainDao;

    @Override
    @SuppressWarnings("unchecked")
    public CrmCustomerMainDao getDao() {
        return crmCustomerMainDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapperBean) {
        CrmCustomerMainBean crmCustomerMainBean = wrapperBean.parseUnique(CrmCustomerMainBean.class);
        String id = ApplicationContextHelper.getNextIdentity() + "";
        crmCustomerMainBean.setId(id);
        getDao().insert(crmCustomerMainBean);
        return id;
    }

}
