/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.controller.impl;

import com.sunwayworld.cloud.module.eln.controller.CoreElnDsController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreElnDsControllerImpl
implements CoreElnDsController {
    @Override
    @Log(value="eln\u6570\u636e\u6e90\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/eln/eln-dses/core-eln-ds-edit-list";
    }

    @Override
    @Log(value="eln\u6570\u636e\u6e90\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/eln/eln-dses/core-eln-ds-detail";
    }

    @Override
    @Log(value="eln\u6570\u636e\u6e90\u9009\u62e9\u5f39\u7a97", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/eln/eln-dses/core-eln-ds-choose-list";
    }
}
