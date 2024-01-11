package com.sunwayworld.emc.module.bp.edit.projectscheme;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhengxy@sunwayworld.com 2021年11月24日
 */
@RequestMapping("/secure/emc/module/bp/edit/project-schemes/page")
public interface PfProjectSchemeController extends GenericEditListPageController {

    @RequestMapping("/choose-scheme")
    String chooseSchemePage();

    @RequestMapping("/choose-scheme-test")
    String chooseSchemeTestPage();
}
