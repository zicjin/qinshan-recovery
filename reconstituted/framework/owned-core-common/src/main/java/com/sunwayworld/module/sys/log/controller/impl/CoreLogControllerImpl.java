/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.log.controller.CoreLogController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreLogControllerImpl
implements CoreLogController {
    @Override
    @Log(value="\u7cfb\u7edf\u65e5\u5fd7\u67e5\u8be2\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/sys/logs/core-log-search-list";
    }
}
