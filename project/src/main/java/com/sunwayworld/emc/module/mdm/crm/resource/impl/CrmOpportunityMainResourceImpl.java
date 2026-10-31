package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.resource.CrmOpportunityMainResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityMainService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@GikamBean
@RestController
@LogModule(value = "商机基本信息")
public class CrmOpportunityMainResourceImpl implements CrmOpportunityMainResource {

    @Autowired
    private CrmOpportunityMainService crmOpportunityMainService;

    @Override
    public CrmOpportunityMainService getService() {
        return crmOpportunityMainService;
    }

}
