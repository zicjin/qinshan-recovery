package com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.controller;



import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 容器类型 Controller
 *
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/equipt-temp/page")
public interface PfEquiptTempController extends GenericEditListPageController {

    @RequestMapping("/edit-list")
    String editListPage();
    
}
