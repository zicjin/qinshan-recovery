package com.sunwayworld.emc.module.bp.edit.checkmethod.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.edit.checkmethod.EmcCheckMethodController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcCheckMethodControllerImpl implements EmcCheckMethodController {

    @Override
    @Log(value = "检测方法修改页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/edit/check-methods/emc-check-method-edit-list";
    }

}
