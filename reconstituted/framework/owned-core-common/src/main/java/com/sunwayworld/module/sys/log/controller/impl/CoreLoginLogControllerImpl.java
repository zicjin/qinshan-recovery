/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.log.controller.CoreLoginLogController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreLoginLogControllerImpl
implements CoreLoginLogController {
    @Override
    @Log(value="\u7cfb\u7edf\u767b\u5f55\u65e5\u5fd7\u67e5\u8be2\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/sys/login-logs/core-login-log-search-list";
    }

    @Override
    @Log(value="\u5728\u7ebf\u7528\u6237\u767b\u5f55\u65e5\u5fd7\u67e5\u8be2\u9875", type=LogType.CONTROLLER)
    public String onlineListPage() {
        return "module/sys/login-logs/core-login-log-online-list";
    }
}
