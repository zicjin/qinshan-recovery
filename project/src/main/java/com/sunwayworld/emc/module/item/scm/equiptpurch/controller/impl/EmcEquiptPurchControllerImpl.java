package com.sunwayworld.emc.module.item.scm.equiptpurch.controller.impl;


import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.scm.equiptpurch.controller.EmcEquiptPurchController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptPurchControllerImpl implements EmcEquiptPurchController{

    @Log(value = "仪器采购编制页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/scm/equipt-purchs/emc-equipt-purch-edit-list";
    }


    @Log(value = "仪器采购验收页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/scm/equipt-purchs/emc-equipt-purch-check-list";
    }


    @Log(value = "仪器采购详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/item/scm/equipt-purchs/emc-equipt-purch-detail";
    }

}
