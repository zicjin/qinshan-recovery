/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.samplescheme.impl;

import com.sunwayworld.cloud.module.item.controller.samplescheme.CoreSampleSchemeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreSampleSchemeControllerImpl
implements CoreSampleSchemeController {
    @Override
    @Log(value="\u62bd\u6837\u65b9\u6848\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/sample-schemes/core-sample-scheme-edit-list";
    }
}
