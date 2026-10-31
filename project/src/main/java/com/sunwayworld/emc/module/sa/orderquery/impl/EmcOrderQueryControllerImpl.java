package com.sunwayworld.emc.module.sa.orderquery.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.sa.orderquery.EmcOrderQueryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2020年02月27日
 */
@Controller
@GikamBean
public class EmcOrderQueryControllerImpl implements EmcOrderQueryController {

    @Override
    @Log(value = "样品数据查询页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/sa/order-queries/emc-order-query-edit-list";
    }
}
