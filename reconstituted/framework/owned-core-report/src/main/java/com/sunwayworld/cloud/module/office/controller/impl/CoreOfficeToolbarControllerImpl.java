/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.controller.impl;

import com.sunwayworld.cloud.module.office.controller.CoreOfficeToolbarController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@GikamBean
@Controller
public class CoreOfficeToolbarControllerImpl
implements CoreOfficeToolbarController {
    @Override
    @Log(value="\u5de5\u5177\u680f\u83dc\u5355\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/office/toolbars/core-office-toolbar-edit-list";
    }
}
