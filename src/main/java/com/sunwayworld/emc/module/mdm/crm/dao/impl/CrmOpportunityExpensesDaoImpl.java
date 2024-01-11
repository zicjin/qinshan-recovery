package com.sunwayworld.emc.module.mdm.crm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityExpensesBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityExpensesDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmOpportunityExpensesMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityExpensesDaoImpl extends MybatisDaoSupport<CrmOpportunityExpensesBean, String> implements CrmOpportunityExpensesDao {

    @Autowired
    private CrmOpportunityExpensesMapper crmOpportunityExpensesMapper;

    @Override
    public CrmOpportunityExpensesMapper getMapper() {
        return crmOpportunityExpensesMapper;
    }
}
