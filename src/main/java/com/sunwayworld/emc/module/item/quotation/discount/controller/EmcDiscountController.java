package com.sunwayworld.emc.module.item.quotation.discount.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;

@RequestMapping("/secure/emc/module/item/quotation/discounts/page")
public interface EmcDiscountController extends GenericEditListPageController, GenericChoosePageController {
}
