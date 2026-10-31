package com.sunwayworld.emc.module.bp.project.plan.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2020-02-10
 */

@RequestMapping("/secure/emc/module/bp/project/plans/page")
public interface EmcProjectPlanController extends GenericEditListPageController {

    @RequestMapping("/schedule")
    public String schedulePage();

    @RequestMapping("/copy-plan")
    String copyPlanPage();
}
