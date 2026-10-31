package com.sunwayworld.emc.module.item.quotation.discount.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.quotation.discount.controller.EmcDiscountController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcDiscountControllerImpl implements EmcDiscountController {

    @Log(value = "折扣管理表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/quotation/discounts/emc-discount-edit-list";
    }

    @Log(value = "折扣管理表选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/item/quotation/discounts/emc-discount-choose-list";
    }

}
