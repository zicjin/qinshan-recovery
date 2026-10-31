package com.sunwayworld.emc.module.bp.order.orderretain.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.order.orderretain.controller.EmcOrderRetainController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2019年10月17日
*/
@Controller
@GikamBean
public class EmcOrderRetainControllerImpl implements EmcOrderRetainController {

    @Override
    @Log(value = "留样编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/order/order-retains/emc-order-retain-edit-list";
    }

    @Override
    @Log(value = "留样选择列表页面", type = LogType.CONTROLLER)
    public String orderRetainChooseListPage() {
        return "emc/module/bp/order/order-retains/emc-order-retain-choose-list";
    }

    @Override
    @Log(value = "留样处置查看表页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/order/order-retains/emc-order-retain-search-list";
    }

    @Override
    @Log(value = "留样审核列表页面", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/bp/order/order-retains/emc-order-retain-audit-list";
    }
}
