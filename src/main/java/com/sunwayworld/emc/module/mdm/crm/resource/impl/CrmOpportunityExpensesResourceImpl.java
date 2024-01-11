package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.resource.CrmOpportunityExpensesResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityExpensesService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@GikamBean
@RestController
@LogModule(value = "商机费用")
public class CrmOpportunityExpensesResourceImpl implements CrmOpportunityExpensesResource {

    @Autowired
    private CrmOpportunityExpensesService crmOpportunityExpensesService;

    @Override
    public CrmOpportunityExpensesService getService() {
        return crmOpportunityExpensesService;
    }

}
