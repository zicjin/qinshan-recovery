package com.sunwayworld.emc.module.mdm.file.file.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@RequestMapping("/secure/emc/module/mdm/file/files/page")
public interface EmcFileController extends GenericEditListPageController, GenericDetailPageController, GenericSearchListPageController, GenericChoosePageController {

    String viewPage();
}
