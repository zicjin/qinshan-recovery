package com.sunwayworld.emc.module.bp.order.orderhandover.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.order.orderhandover.controller.EmcOrderHandoverController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcOrderHandoverControllerImpl implements EmcOrderHandoverController {

    @Override
    @Log(value = "样品交接单查询页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/order/order-handovers/emc-order-handover-search-list";
    }

    @Override
    @Log(value = "样品交接单-采样单附件信息列表页", type = LogType.CONTROLLER)
    public String sampleAnnexListPage() {
        return "emc/module/bp/order/order-handovers/emc-order-handover-sample-annex";
    }

}
