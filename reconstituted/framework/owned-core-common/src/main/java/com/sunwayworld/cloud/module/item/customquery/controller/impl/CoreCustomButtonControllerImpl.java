/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.controller.impl;

import com.sunwayworld.cloud.module.item.customquery.controller.CoreCustomButtonController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreCustomButtonControllerImpl
implements CoreCustomButtonController {
    @Override
    @Log(value="\u5168\u5c40\u6309\u94ae\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/custom-buttons/core-custom-button-edit-list";
    }

    @Override
    @Log(value="\u5168\u5c40\u6309\u94ae\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/item/custom-buttons/core-custom-button-choose-list";
    }
}
