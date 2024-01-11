package com.sunwayworld.emc.module.mdm.invoices.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/mdm/invoices/page")
public interface EmcInvoiceController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {
}
