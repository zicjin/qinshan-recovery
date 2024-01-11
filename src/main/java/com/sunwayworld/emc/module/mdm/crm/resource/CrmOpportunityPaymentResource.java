package com.sunwayworld.emc.module.mdm.crm.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityPaymentBean;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityPaymentService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@RequestMapping("/secure/emc/module/mdm/crm/payments")
public interface CrmOpportunityPaymentResource extends GenericResource<CrmOpportunityPaymentService, CrmOpportunityPaymentBean, String> {

}
