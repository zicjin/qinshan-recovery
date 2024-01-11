package com.sunwayworld.emc.module.item.quotation.addprice.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;

@RequestMapping("/secure/emc/module/item/quotation/addprices/page")
public interface EmcAddpriceController extends GenericEditListPageController, GenericChoosePageController {
}
