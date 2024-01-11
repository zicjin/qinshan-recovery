package com.sunwayworld.emc.module.mdm.crm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmCustomerMainBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmCustomerMainDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmCustomerMainMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmCustomerMainDaoImpl extends MybatisDaoSupport<CrmCustomerMainBean, String> implements CrmCustomerMainDao {

    @Autowired
    private CrmCustomerMainMapper crmCustomerMainMapper;

    @Override
    public CrmCustomerMainMapper getMapper() {
        return crmCustomerMainMapper;
    }
}
