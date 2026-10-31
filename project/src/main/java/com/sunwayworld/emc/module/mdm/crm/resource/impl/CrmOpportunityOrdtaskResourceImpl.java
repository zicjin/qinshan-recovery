package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.resource.CrmOpportunityOrdtaskResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOrdtaskService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@GikamBean
@RestController
@LogModule(value = "商机明细")
public class CrmOpportunityOrdtaskResourceImpl implements CrmOpportunityOrdtaskResource {

    @Autowired
    private CrmOpportunityOrdtaskService crmOpportunityOrdtaskService;

    @Override
    public CrmOpportunityOrdtaskService getService() {
        return crmOpportunityOrdtaskService;
    }

}
