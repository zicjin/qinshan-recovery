package com.sunwayworld.emc.module.mdm.bizmdm.client.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@RequestMapping("/secure/emc/module/mdm/bizmdm/clients/page")
public interface EmcClientController extends GenericEditListPageController, GenericChoosePageController, GenericDetailPageController {

    @RequestMapping("/monitor-point-choose-list")
    public String monitorPointchoosePage();

    @RequestMapping("/monitor-point-simple-choose-list")
    public String monitorPointSimpleChoosePage();

    @RequestMapping("/monitor-point-edit-list")
    public String monitorPointEditPage();

    @RequestMapping("/contact-choose-list")
    public String contactChoosePage();

    @RequestMapping("/sub-choose-list")
    public String subClientChoosePage();
}
