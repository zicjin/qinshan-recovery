/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.controller.impl;

import com.sunwayworld.cloud.module.office.controller.CoreOfficeTmplController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreOfficeTmplControllerImpl
implements CoreOfficeTmplController {
    @Override
    @Log(value="\u62a5\u544a\u6a21\u677f\u8868\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/office/tmpls/core-office-tmpl-edit-list";
    }

    @Override
    @Log(value="\u62a5\u544a\u6a21\u677f\u8868\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/office/tmpls/core-office-tmpl-detail";
    }

    @Override
    @Log(value="\u62a5\u544a\u6a21\u677f\u8868\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/office/tmpls/core-office-tmpl-choose-list";
    }

    @Override
    @Log(value="\u62a5\u544a\u6a21\u677f\u8868\u6d4b\u8bd5\u6a21\u677f", type=LogType.CONTROLLER)
    public String testTmplPage() {
        return "module/office/tmpls/core-office-tmpl-test";
    }
}
