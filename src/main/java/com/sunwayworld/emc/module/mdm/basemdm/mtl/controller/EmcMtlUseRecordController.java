package com.sunwayworld.emc.module.mdm.basemdm.mtl.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/mdm/basemdm/mtl-use-records/page")
public interface EmcMtlUseRecordController extends GenericEditListPageController,
                                                   GenericDetailPageController,
                                                   GenericSearchListPageController,
                                                   GenericChoosePageController {
	 @RequestMapping("/{id}/apply")
	 String applyPage();

}
