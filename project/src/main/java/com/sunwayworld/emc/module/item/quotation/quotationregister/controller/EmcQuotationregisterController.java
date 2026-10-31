package com.sunwayworld.emc.module.item.quotation.quotationregister.controller;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;

@RequestMapping("/secure/emc/module/item/quotation/quotationregisters/page")
public interface EmcQuotationregisterController extends GenericEditListPageController, GenericDetailPageController , GenericAuditListPageController, GenericSearchListPageController {
    @RequestMapping("/batch-update-information")
    public String batchUpdateInformationPage();
}
