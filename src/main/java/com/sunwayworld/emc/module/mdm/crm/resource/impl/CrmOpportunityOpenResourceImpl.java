package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.emc.module.mdm.crm.resource.CrmOpportunityOpenResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOpenService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年08月30日
 */
@GikamBean
@RestController
@LogModule(value = "商机合同")
public class CrmOpportunityOpenResourceImpl implements CrmOpportunityOpenResource {

    @Autowired
    private CrmOpportunityOpenService opportunityOpenService;

    @Override
    @Log(value = "接收CRM商机基本信息", type = LogType.INSERT)
    @RequestMapping(value = "/receive/opportunity-main", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveOpportunityMain(RestJsonWrapperBean wrapper) {
        return opportunityOpenService.receiveOpportunityMain(wrapper);
    }

    @Override
    @Log(value = "接收CRM商机合同信息", type = LogType.INSERT)
    @RequestMapping(value = "/receive/opportunity-contract", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveOpportunityContract(RestJsonWrapperBean wrapper) {
        return opportunityOpenService.receiveOpportunityContract(wrapper);
    }

    @Override
    @Log(value = "接收CRM商机费用信息", type = LogType.INSERT)
    @RequestMapping(value = "/receive/opportunity-expenses", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveOpportunityExpenses(RestJsonWrapperBean wrapper) {
        return opportunityOpenService.receiveOpportunityExpenses(wrapper);
    }

    @Override
    @Log(value = "接收CRM商机支付信息", type = LogType.INSERT)
    @RequestMapping(value = "/receive/opportunity-payment", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveOpportunityPayment(RestJsonWrapperBean wrapper) {
        return opportunityOpenService.receiveOpportunityPayment(wrapper);
    }

    @Override
    @Log(value = "接收CRM商机检品信息", type = LogType.INSERT)
    @RequestMapping(value = "/receive/opportunity-folders", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveOpportunityFolders(RestJsonWrapperBean wrapper) {
        return opportunityOpenService.receiveOpportunityFolders(wrapper);
    }

    @Override
    @Log(value = "接收CRM商机样号信息", type = LogType.INSERT)
    @RequestMapping(value = "/receive/opportunity-orders", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveOpportunityOrders(RestJsonWrapperBean wrapper) {
        return opportunityOpenService.receiveOpportunityOrders(wrapper);
    }

    @Override
    @Log(value = "接收CRM商机项目信息", type = LogType.INSERT)
    @RequestMapping(value = "/receive/opportunity-ordtask", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveOpportunityOrdtask(RestJsonWrapperBean wrapper) {
        return opportunityOpenService.receiveOpportunityOrdtask(wrapper);
    }

    @Override
    @Log(value = "接收CRM商机附件信息", type = LogType.INSERT)
    @RequestMapping(value = "/receive/opportunity-files", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveOpportunityFiles(RestJsonWrapperBean wrapper) {
        return opportunityOpenService.receiveOpportunityFiles(wrapper);
    }
}
