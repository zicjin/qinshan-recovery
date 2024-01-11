package com.sunwayworld.emc.module.mdm.crm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityPaymentBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityPaymentDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmOpportunityPaymentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityPaymentDaoImpl extends MybatisDaoSupport<CrmOpportunityPaymentBean, String> implements CrmOpportunityPaymentDao {

    @Autowired
    private CrmOpportunityPaymentMapper crmOpportunityPaymentMapper;

    @Override
    public CrmOpportunityPaymentMapper getMapper() {
        return crmOpportunityPaymentMapper;
    }
}
