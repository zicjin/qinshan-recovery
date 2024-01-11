package com.sunwayworld.emc.module.mdm.crm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityMainDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmOpportunityMainMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityMainDaoImpl extends MybatisDaoSupport<CrmOpportunityMainBean, String> implements CrmOpportunityMainDao {

    @Autowired
    private CrmOpportunityMainMapper crmOpportunityMainMapper;

    @Override
    public CrmOpportunityMainMapper getMapper() {
        return crmOpportunityMainMapper;
    }
}
