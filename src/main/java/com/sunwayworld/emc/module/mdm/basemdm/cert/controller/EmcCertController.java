package com.sunwayworld.emc.module.mdm.basemdm.cert.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/emp-certs/page")
public interface EmcCertController extends GenericEditListPageController, GenericChoosePageController, GenericDetailPageController {
}
