package com.sunwayworld.emc.module.bp.order.orderreceive.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/secure/emc/module/bp/order/order-receive/page")
public interface PfOrderReceiveController extends GenericEditListPageController, GenericSearchListPageController {
}
