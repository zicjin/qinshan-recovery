/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.config.controller.CoreSystemConfigController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreSystemConfigControllerImpl
implements CoreSystemConfigController {
    @Override
    @Log(value="\u7cfb\u7edf\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/system-configs/core-system-config-edit-list";
    }
}
