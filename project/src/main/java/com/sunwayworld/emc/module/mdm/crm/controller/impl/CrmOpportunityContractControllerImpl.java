package com.sunwayworld.emc.module.mdm.crm.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.crm.controller.CrmOpportunityContractController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Controller
@GikamBean
public class CrmOpportunityContractControllerImpl implements CrmOpportunityContractController {

    @Override
    @Log(value = "商机合同编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/crms/crm-opportunity-contract-edit-list";
    }

    @Override
    @Log(value = "检测点位选择页面", type = LogType.CONTROLLER)
    public String orderChooseListPage() {
        return "emc/module/mdm/crms/crm-opportunity-order-choose-list";
    }

    @Override
    @Log(value = "合同详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/crms/crm-opportunity-contract-detail-list";
    }
}
