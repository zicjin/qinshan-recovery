/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.controller.impl;

import com.sunwayworld.cloud.module.eln.controller.CoreElnTmplController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreElnTmplControllerImpl
implements CoreElnTmplController {
    @Override
    @Log(value="eln\u6a21\u677f\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/eln/eln-tmpls/core-eln-tmpl-edit-list";
    }

    @Override
    @Log(value="eln\u6a21\u677f\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/eln/eln-tmpls/core-eln-tmpl-detail";
    }

    @Override
    @Log(value="eln\u6a21\u677f\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/eln/eln-tmpls/core-eln-tmpl-choose-list";
    }

    @Override
    @Log(value="eln\u6a21\u677f\u6d4b\u8bd5\u9875", type=LogType.CONTROLLER)
    public String testTmplPage() {
        return "module/eln/eln-tmpls/core-eln-tmpl-test";
    }
}
