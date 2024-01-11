package com.sunwayworld.emc.module.bp.order.orderhandover.impl;


import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.order.orderhandover.PfOrderHandoverController;
import com.sunwayworld.emc.module.bp.order.orderhandover.controller.impl.EmcOrderHandoverControllerImpl;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2021年12月17日
 */
@Controller
@GikamBean
public class PfOrderHandoverControllerImpl extends EmcOrderHandoverControllerImpl implements PfOrderHandoverController {

    @Override
    @Log(value = "项目方案打印", type = LogType.CONTROLLER)
    public String searchListAllPage() {
        return "emc/module/bp/order/order-handovers/emc-order-handover-search-all-list";
    }
}
