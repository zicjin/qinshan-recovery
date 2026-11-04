/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.controller.impl;

import com.sunwayworld.cloud.module.item.customquery.controller.CoreCustomQueryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreCustomQueryControllerImpl
implements CoreCustomQueryController {
    @Override
    @Log(value="\u81ea\u5b9a\u4e49\u67e5\u8be2\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/custom-queries/core-custom-query-edit-list";
    }

    @Override
    @Log(value="\u81ea\u5b9a\u4e49\u67e5\u8be2\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/item/custom-queries/core-custom-query-detail";
    }

    @Override
    @Log(value="\u67e5\u8be2\u5b57\u6bb5\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String fieldChoosePage() {
        return "module/item/custom-queries/core-custom-query-field-choose-list";
    }

    @Override
    @Log(value="\u53d1\u5e03grid\u83dc\u5355\u9875", type=LogType.CONTROLLER)
    public String gridMenuPage() {
        return "module/item/custom-queries/core-custom-query-grid-menu-list";
    }

    @Override
    @Log(value="\u53d1\u5e03\u5e06\u8f6f\u83dc\u5355\u9875", type=LogType.CONTROLLER)
    public String reportMenuPage() {
        return "module/item/custom-queries/core-custom-query-report-menu-list";
    }
}
