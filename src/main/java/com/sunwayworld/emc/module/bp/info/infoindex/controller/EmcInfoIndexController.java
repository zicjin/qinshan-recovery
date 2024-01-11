package com.sunwayworld.emc.module.bp.info.infoindex.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */

@RequestMapping("/secure/emc/module/bp/info/info-indexs/page")
public interface EmcInfoIndexController extends GenericEditListPageController, GenericChoosePageController {
}
