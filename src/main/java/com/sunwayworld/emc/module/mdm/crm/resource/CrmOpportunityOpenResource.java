package com.sunwayworld.emc.module.mdm.crm.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年08月30日
 */
@RequestMapping("/open/emc/module/mdm/crm/opportunity")
public interface CrmOpportunityOpenResource {

    CrmOpportunityResponseDTO receiveOpportunityMain(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityContract(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityExpenses(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityPayment(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityFolders(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityOrders(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityOrdtask(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityFiles(RestJsonWrapperBean wrapper);
}
