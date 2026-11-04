/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.remotecall.controller.CoreRemoteCallConfigController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreRemoteCallConfigControllerImpl
implements CoreRemoteCallConfigController {
    @Override
    @Log(value="\u8fdc\u7a0b\u8c03\u7528\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875")
    public String editListPage() {
        return "module/sys/remote-calls/core-remote-call-config-edit-list";
    }

    @Override
    @Log(value="\u8fdc\u7a0b\u8c03\u7528\u8bf7\u6c42\u8be6\u60c5\u9875")
    public String requestDetailPage() {
        return "module/sys/remote-calls/core-remote-call-request-detail";
    }
}
