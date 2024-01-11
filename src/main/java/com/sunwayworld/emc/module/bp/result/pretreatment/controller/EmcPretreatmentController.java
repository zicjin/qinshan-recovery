package com.sunwayworld.emc.module.bp.result.pretreatment.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@RequestMapping("/secure/emc/module/bp/result/pretreatments/page")
public interface EmcPretreatmentController extends GenericEditListPageController, GenericChoosePageController {

    String extractMethodChoosePage();

    String purifyMethodChoosePage();
}
