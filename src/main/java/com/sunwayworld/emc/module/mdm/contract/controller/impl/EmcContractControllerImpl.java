package com.sunwayworld.emc.module.mdm.contract.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.contract.controller.EmcContractController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcContractControllerImpl implements EmcContractController {

    @Log(value = "合同表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/contracts/emc-contract-edit-list";
    }

    @Log(value = "合同表详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/contracts/emc-contract-detail";
    }

    @Log(value = "合同表查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/mdm/contracts/emc-contract-search-list";
    }

    @Log(value = "合同表选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/contracts/emc-contract-choose-list";
    }

    @Override
    @Log(value = "合同复制列表页", type = LogType.CONTROLLER)
    public String copyListPage() {
        return "emc/module/mdm/contracts/emc-contract-copy-list";
    }

    @Log(value = "合同编辑列表页", type = LogType.CONTROLLER)
    @Override
    public String redactListPage() {
        return "emc/module/mdm/contracts/emc-contract-redact-list";
    }

}
