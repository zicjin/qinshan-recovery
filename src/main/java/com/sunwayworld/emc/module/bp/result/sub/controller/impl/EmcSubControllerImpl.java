package com.sunwayworld.emc.module.bp.result.sub.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.result.sub.controller.EmcSubController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcSubControllerImpl implements EmcSubController {

    @Override
    @Log(value = "分包数据录入页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/result/subs/emc-sub-result-edit-list";
    }

}
