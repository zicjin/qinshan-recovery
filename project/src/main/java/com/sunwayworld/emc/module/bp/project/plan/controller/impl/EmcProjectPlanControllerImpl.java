package com.sunwayworld.emc.module.bp.project.plan.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.project.plan.controller.EmcProjectPlanController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2020-02-10
 */

@Controller
@GikamBean
public class EmcProjectPlanControllerImpl implements EmcProjectPlanController {

    @Log(value = "任务计划列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/project/plans/emc-project-plan-edit-list";
    }

    @Log(value = "定时器信息详情", type = LogType.CONTROLLER)
    @Override
    public String schedulePage() {
        return "emc/module/bp/project/plans/emc-project-plan-schedule";
    }

    @Log(value = "复制任务计划页面", type = LogType.CONTROLLER)
    @Override
    public String copyPlanPage() {
        return "emc/module/bp/project/plans/emc-project-plan-copy";
    }
}
