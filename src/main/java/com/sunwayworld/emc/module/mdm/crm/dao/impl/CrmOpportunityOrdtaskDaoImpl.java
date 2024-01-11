package com.sunwayworld.emc.module.mdm.crm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityOrdtaskBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityOrdtaskDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmOpportunityOrdtaskMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityOrdtaskDaoImpl extends MybatisDaoSupport<CrmOpportunityOrdtaskBean, String> implements CrmOpportunityOrdtaskDao {

    @Autowired
    private CrmOpportunityOrdtaskMapper crmOpportunityOrdtaskMapper;

    @Override
    public CrmOpportunityOrdtaskMapper getMapper() {
        return crmOpportunityOrdtaskMapper;
    }
}
