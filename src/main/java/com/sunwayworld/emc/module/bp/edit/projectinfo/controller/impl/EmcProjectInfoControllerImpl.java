package com.sunwayworld.emc.module.bp.edit.projectinfo.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.edit.projectinfo.controller.EmcProjectInfoController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2019年11月06日
 */
@Controller
@GikamBean
public class EmcProjectInfoControllerImpl implements EmcProjectInfoController {

    @Log(value = "项目信息编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/edit/project-infos/emc-project-info-edit-list";
    }

    @Override
    @Log(value = "容器修改列表页", type = LogType.CONTROLLER)
    public String containerPage() {
        return "emc/module/bp/edit/project-infos/emc-project-info-container";
    }
}
