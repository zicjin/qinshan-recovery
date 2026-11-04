/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.controller.impl;

import com.sunwayworld.cloud.module.office.controller.CoreOfficeDsController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreOfficeDsControllerImpl
implements CoreOfficeDsController {
    @Override
    @Log(value="\u62a5\u544a\u6570\u636e\u6e90\u8868\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/office/dses/core-office-ds-edit-list";
    }

    @Override
    @Log(value="\u62a5\u544a\u6570\u636e\u6e90\u8868\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/office/dses/core-office-ds-detail";
    }
}
