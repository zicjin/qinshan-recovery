/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.chart.impl;

import com.sunwayworld.cloud.module.item.controller.chart.CoreChartController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class CoreChartControllerImpl
implements CoreChartController {
    @Override
    @Log(value="\u56fe\u8868\u7f16\u5236\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/charts/chart-edit-list";
    }

    @Override
    @Log(value="\u56fe\u8868\u7ef4\u62a4\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/maintain-list"})
    public String maintainListPage() {
        return "module/item/charts/chart-maintain-list";
    }

    @Override
    @Log(value="\u56fe\u8868\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/item/charts/chart-detail";
    }

    @Override
    @Log(value="\u56fe\u8868\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/item/charts/chart-search-list";
    }

    @Override
    @RequestMapping(value={"/display"})
    @Log(value="\u56fe\u8868\u5c55\u793a\u9875\u9762", type=LogType.CONTROLLER)
    public String displayPage() {
        return "module/item/charts/chart-display";
    }

    @Override
    @RequestMapping(value={"/echarts"})
    @Log(value="\u56fe\u8868\u5c55\u793a\u9875\u9762", type=LogType.CONTROLLER)
    public String echarts() {
        return "module/item/echarts/echarts";
    }
}
