package com.sunwayworld.emc.module.mdm.contract.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.contract.controller.PfContractController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年11月08日
 */
@Controller
@GikamBean
public class PfContractControllerImpl extends EmcContractControllerImpl implements PfContractController {

    @Override
    @Log(value = "拆分子合同选择页面", type = LogType.CONTROLLER)
    public String splitContractChoosePage() {
        return "emc/module/mdm/contracts/emc-split-contract-choose-list";
    }
}
