/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.controller.impl;

import com.sunwayworld.cloud.module.item.customchart.controller.CoreCustomChartShareController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreCustomChartShareControllerImpl
implements CoreCustomChartShareController {
    @Override
    @Log(value="\u5171\u4eab\u94fe\u63a5", type=LogType.CONTROLLER)
    public String displayLinkPage() {
        return "module/item/custom-charts/core-custom-chart-display-link";
    }
}
