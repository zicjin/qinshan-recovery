package com.sunwayworld.emc.module.mdm.invoices.controller.impl;

import com.sunwayworld.emc.module.mdm.invoices.controller.EmcInvoiceController;
import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcInvoiceControllerImpl implements EmcInvoiceController {

    @Log(value = "开票记录编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/invoices/emc-invoice-edit-list";
    }

    @Log(value = "开票记录审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/mdm/invoices/emc-invoice-audit-list";
    }

    @Log(value = "开票记录查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/mdm/invoices/emc-invoice-search-list";
    }

}
