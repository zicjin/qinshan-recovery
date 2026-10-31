package com.sunwayworld.emc.module.item.scm.purchmanage.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.scm.purchmanage.controller.EmcPurchManageController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcPurchManageControllerImpl implements EmcPurchManageController {

    @Log(value = "采购管理审核页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/scm/purch-manages/emc-purch-manage-audit-list";
    }

    @Log(value = "采购管理查询页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/item/scm/purch-manages/emc-purch-manage-search-list";
    }

}
