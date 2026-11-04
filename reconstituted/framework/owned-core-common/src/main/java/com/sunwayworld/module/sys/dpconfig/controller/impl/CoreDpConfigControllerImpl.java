/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.dpconfig.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.dpconfig.controller.CoreDpConfigController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreDpConfigControllerImpl
implements CoreDpConfigController {
    @Override
    @Log(value="\u6570\u636e\u6cf5\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/dp-configs/core-dp-config-edit-list";
    }

    @Override
    @Log(value="\u6570\u636e\u6cf5\u914d\u7f6e\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/sys/dp-configs/core-dp-config-choose-list";
    }
}
