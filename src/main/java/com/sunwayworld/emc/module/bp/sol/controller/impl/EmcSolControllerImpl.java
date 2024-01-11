package com.sunwayworld.emc.module.bp.sol.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.sol.controller.EmcSolController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcSolControllerImpl implements EmcSolController {

    @Log(value = "标准溶液配制编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/sols/emc-sol-edit-list";
    }

    @Log(value = "标准溶液配制详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/bp/sols/emc-sol-detail";
    }

}
