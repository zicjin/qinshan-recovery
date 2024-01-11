package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.resource.CrmOpportunityOrdersResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOrdersService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@GikamBean
@RestController
@LogModule(value = "商机orders")
public class CrmOpportunityOrdersResourceImpl implements CrmOpportunityOrdersResource {

    @Autowired
    private CrmOpportunityOrdersService crmOpportunityOrdersService;

    @Override
    public CrmOpportunityOrdersService getService() {
        return crmOpportunityOrdersService;
    }

}
