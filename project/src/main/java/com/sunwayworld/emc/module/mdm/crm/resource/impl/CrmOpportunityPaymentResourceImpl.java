package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.resource.CrmOpportunityPaymentResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityPaymentService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@GikamBean
@RestController
@LogModule(value = "商机支付")
public class CrmOpportunityPaymentResourceImpl implements CrmOpportunityPaymentResource {

    @Autowired
    private CrmOpportunityPaymentService crmOpportunityPaymentService;

    @Override
    public CrmOpportunityPaymentService getService() {
        return crmOpportunityPaymentService;
    }

}
