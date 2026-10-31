package com.sunwayworld.emc.module.mdm.basemdm.filecategory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhengxy@sunwayworld.com 2019年10月9日
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/file-categories/page")
public interface EmcFileCategoryController extends GenericEditListPageController,
                                                   GenericDetailPageController {
}
