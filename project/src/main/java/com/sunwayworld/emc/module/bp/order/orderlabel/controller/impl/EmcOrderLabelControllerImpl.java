package com.sunwayworld.emc.module.bp.order.orderlabel.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.order.orderlabel.controller.EmcOrderLabelController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2019年10月18日
*/
@Controller
@GikamBean
public class EmcOrderLabelControllerImpl implements EmcOrderLabelController {

    @Override
    @Log(value = "样品标签编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/order/order-labels/emc-order-label-edit-list";
    }
    
    @Override
    @Log(value = "样品标签查看列表页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/order/order-labels/emc-order-label-search-list";
    }

}
