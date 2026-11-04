/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.controller.impl;

import com.sunwayworld.cloud.module.office.controller.CoreOfficeCustomizationController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@GikamBean
@Controller
public class CoreOfficeCustomizationControllerImpl
implements CoreOfficeCustomizationController {
    @Override
    @Log(value="\u4e2a\u6027\u5316\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/office/customizations/core-office-customization-edit-list";
    }
}
