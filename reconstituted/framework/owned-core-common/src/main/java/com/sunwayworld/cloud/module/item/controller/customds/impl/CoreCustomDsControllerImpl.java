/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.customds.impl;

import com.sunwayworld.cloud.module.item.controller.customds.CoreCustomDsController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreCustomDsControllerImpl
implements CoreCustomDsController {
    @Override
    @Log(value="\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u8868\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/custom-dses/core-custom-ds-edit-list";
    }

    @Override
    @Log(value="\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u8868\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/item/custom-dses/core-custom-ds-detail";
    }

    @Override
    @Log(value="\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/item/custom-dses/core-custom-ds-choose-list";
    }

    @Override
    @Log(value="\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u65b0\u589e\u9875\u9762", type=LogType.CONTROLLER)
    public String addListPage() {
        return "module/item/custom-dses/core-custom-ds-add";
    }
}
