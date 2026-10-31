package com.sunwayworld.emc.module.bp.edit.checkproject.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.edit.checkproject.EmcCheckProjectController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcCheckProjectControllerImpl implements EmcCheckProjectController {

    @Override
    @Log(value = "检测项目修改页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/edit/check-projects/emc-check-project-edit-list";
    }

}
