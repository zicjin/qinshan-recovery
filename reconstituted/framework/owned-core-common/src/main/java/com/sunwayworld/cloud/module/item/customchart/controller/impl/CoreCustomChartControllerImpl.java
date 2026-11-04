/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.controller.impl;

import com.sunwayworld.cloud.module.item.customchart.controller.CoreCustomChartController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreCustomChartControllerImpl
implements CoreCustomChartController {
    @Override
    @Log(value="\u81ea\u5b9a\u4e49\u56fe\u8868\u7684\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/custom-charts/core-custom-chart-edit-list";
    }

    @Override
    @Log(value="\u81ea\u5b9a\u4e49\u56fe\u8868\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/item/custom-charts/core-custom-chart-detail";
    }

    @Override
    @Log(value="\u9884\u89c8\u9996\u9875\u754c\u9762", type=LogType.CONTROLLER)
    public String displayPanelPage() {
        return "module/item/custom-charts/core-custom-chart-display-panel";
    }

    @Override
    @Log(value="\u9884\u89c8\u83dc\u5355\u754c\u9762", type=LogType.CONTROLLER)
    public String displayMenulPage() {
        return "module/item/custom-charts/core-custom-chart-display-menu";
    }
}
