package com.sunwayworld.emc.module.bp.delivery.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.delivery.controller.EmcDeliveryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月15日
 */
@Controller
@GikamBean
public class EmcDeliveryControllerImpl implements EmcDeliveryController {

    @Override
    @Log(value = "样品交接", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/deliveries/emc-delivery-edit-list";
    }

    @Override
    @Log(value = "样品选择页面", type = LogType.CONTROLLER)
    @RequestMapping("/order-choose-list")
    public String orderChoosePage() {
        return "emc/module/bp/deliveries/emc-delivery-order-choose-list";
    }

    @Override
    @Log(value = "客户样品接收提交后查看", type = LogType.CONTROLLER)
    @RequestMapping("/audit-search-list")
    public String auditSearchListPage() {
        return "emc/module/bp/deliveries/emc-delivery-audit-list";
    }

    @Override
    @Log(value = "客户样品接收", type = LogType.CONTROLLER)
    @RequestMapping("/order-accept-list")
    public String orderAcceptPage() {
        return "emc/module/bp/deliveries/emc-delivery-order-accept-list";
    }
}
