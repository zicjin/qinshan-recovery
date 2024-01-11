package com.sunwayworld.emc.module.item.quotation.quotationinformation.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.quotation.quotationinformation.controller.EmcQuotationinformationController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcQuotationinformationControllerImpl implements EmcQuotationinformationController {

    @Log(value = "报价单信息表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/quotation/quotationinformations/emc-quotationinformation-edit-list";
    }

}
