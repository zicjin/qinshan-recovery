package com.sunwayworld.emc.module.item.quotation.addprice.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.quotation.addprice.controller.EmcAddpriceController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcAddpriceControllerImpl implements EmcAddpriceController {

    @Log(value = "附加费用表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/quotation/addprices/emc-addprice-edit-list";
    }

    @Log(value = "附加费用表选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/item/quotation/addprices/emc-addprice-choose-list";
    }

}
