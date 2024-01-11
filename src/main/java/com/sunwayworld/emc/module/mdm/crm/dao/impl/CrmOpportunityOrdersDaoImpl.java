package com.sunwayworld.emc.module.mdm.crm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityOrdersBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityOrdersDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmOpportunityOrdersMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityOrdersDaoImpl extends MybatisDaoSupport<CrmOpportunityOrdersBean, String> implements CrmOpportunityOrdersDao {

    @Autowired
    private CrmOpportunityOrdersMapper crmOpportunityOrdersMapper;

    @Override
    public CrmOpportunityOrdersMapper getMapper() {
        return crmOpportunityOrdersMapper;
    }
}
