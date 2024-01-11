package com.sunwayworld.emc.module.sa.projectquery;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/sa/project-queries/page")
public interface EmcProjectQueryController extends GenericEditListPageController{

    @RequestMapping("/cond-query")
    String queryByCondPage();
}
