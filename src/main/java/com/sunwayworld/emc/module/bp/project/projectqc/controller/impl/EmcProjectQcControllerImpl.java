package com.sunwayworld.emc.module.bp.project.projectqc.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.project.projectqc.controller.EmcProjectQcController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年09月21日
*/
@Controller
@GikamBean
public class EmcProjectQcControllerImpl implements EmcProjectQcController {

    @Override
    @Log(value = "项目质控编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/project/project-qcs/emc-project-qc-edit-list";
    }

}
