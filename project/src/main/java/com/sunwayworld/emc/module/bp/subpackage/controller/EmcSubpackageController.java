package com.sunwayworld.emc.module.bp.subpackage.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
* @author zhengxy@sunwayworld.com 2022年09月05日
*/
@RequestMapping("/secure/emc/module/bp/subpackages/page")
public interface EmcSubpackageController extends GenericEditListPageController {

    String acceptPage();
}
