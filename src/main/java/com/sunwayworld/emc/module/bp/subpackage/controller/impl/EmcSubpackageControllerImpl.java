package com.sunwayworld.emc.module.bp.subpackage.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.subpackage.controller.EmcSubpackageController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2022年09月05日
*/
@Controller
@GikamBean
public class EmcSubpackageControllerImpl implements EmcSubpackageController {

    @Override
    @Log(value = "分包单编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/subpackages/emc-subpackage-edit-list";
    }

    @Override
    @RequestMapping({"/accept-list"})
    @Log(value = "分包单接收列表页面", type = LogType.CONTROLLER)
    public String acceptPage() {
        return "emc/module/bp/subpackages/emc-subpackage-accept-list";
    }
}
