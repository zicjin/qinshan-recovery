/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqconfig.controller.impl;

import com.sunwayworld.cloud.module.equipt.acqconfig.controller.CoreEquiptAcqConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreEquiptAcqConfigControllerImpl
implements CoreEquiptAcqConfigController {
    @Override
    @Log(value="\u4eea\u5668\u8fde\u63a5\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String connectListPage() {
        return "module/equipt/acq-configs/core-equipt-acq-config-connect-list";
    }
}
