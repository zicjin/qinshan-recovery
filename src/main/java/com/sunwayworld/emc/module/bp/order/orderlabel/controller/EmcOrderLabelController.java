package com.sunwayworld.emc.module.bp.order.orderlabel.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
* @author zhengxy@sunwayworld.com 2019年10月18日
*/
@RequestMapping("/secure/emc/module/bp/order/order-labels/page")
public interface EmcOrderLabelController extends GenericEditListPageController, GenericSearchListPageController {
}
