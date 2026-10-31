package com.sunwayworld.emc.module.mdm.bizmdm.method.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

@RequestMapping("/secure/emc/module/mdm/bizmdm/methods/page")
public interface EmcMethodController extends GenericEditListPageController, GenericChoosePageController, GenericDetailPageController {

    @RequestMapping("/emc-method-estandard-list")
    public String methodEstandardListPage();
}
