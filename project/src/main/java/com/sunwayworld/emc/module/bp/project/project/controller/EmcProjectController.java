package com.sunwayworld.emc.module.bp.project.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */

@RequestMapping("/secure/emc/module/bp/project/projects/page")
public interface EmcProjectController extends GenericEditListPageController, GenericChoosePageController {

    public String projDetailPage();
    
    @RequestMapping("/order-addition")
    public String orderAdditionPage();
    
    @RequestMapping("/batch-update-order")
    public String batchUpdateOrderPage();

    @RequestMapping("/update-order-container-group")
    public String updateOrderContainerGroupPage();

    @RequestMapping("/test-addition")
    public String testAdditionPage();

    @RequestMapping("/schemeedit-audit-list")
    public String schemeEditPage();

    @RequestMapping("/schemeaudit-audit-list")
    public String schemeAuditPage();

    @RequestMapping("/schemeaudit-query-list")
    public String schemeQueryPage();

    @RequestMapping("/copy-project")
    String copyProjectPage();
    
    @RequestMapping("/prospect")
    String prospectPage();

}
