package com.sunwayworld.emc.module.bp.edit.projectinfo.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhengxy@sunwayworld.com 2019年11月06日
 */
@RequestMapping("/secure/emc/module/bp/edit/project-infos/page")
public interface EmcProjectInfoController extends GenericEditListPageController {

    @RequestMapping("/container")
    String containerPage();
}
