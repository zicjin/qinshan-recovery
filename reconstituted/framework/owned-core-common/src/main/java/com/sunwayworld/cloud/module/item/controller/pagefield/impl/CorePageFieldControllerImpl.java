/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.pagefield.impl;

import com.sunwayworld.cloud.module.item.controller.pagefield.CorePageFieldController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CorePageFieldControllerImpl
implements CorePageFieldController {
    @Override
    @Log(value="\u9875\u9762\u5b57\u6bb5\u914d\u7f6e-\u8868\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/page-fields/core-page-field-edit-list";
    }

    @Override
    @Log(value="\u9875\u9762\u5b57\u6bb5\u914d\u7f6e-\u8868\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/item/page-fields/core-page-field-choose-list";
    }
}
