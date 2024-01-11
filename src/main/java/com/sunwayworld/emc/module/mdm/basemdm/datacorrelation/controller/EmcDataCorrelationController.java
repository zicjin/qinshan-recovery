package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/data-correlations/page")
@GikamBean
public interface EmcDataCorrelationController extends GenericEditListPageController, 
                                                      GenericDetailPageController {
    String scriptPage();
    String chooseItemMultPage();
}
