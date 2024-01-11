package com.sunwayworld.emc.module.bp.order.orderreceive.controller.impl;

import com.sunwayworld.emc.module.bp.order.orderreceive.controller.PfOrderReceiveController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class PfOrderReceiveControllerImpl implements PfOrderReceiveController {

    @Override
    @Log(value = "样品领用编制列表页", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/order/order-receive/emc-order-receive-edit-list";
    }

    @Override
    @Log(value = "样品领用查询界面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/order/order-receive/emc-order-receive-search-list";
    }
}
