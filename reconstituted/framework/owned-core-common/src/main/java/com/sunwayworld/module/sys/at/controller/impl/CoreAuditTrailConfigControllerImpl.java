/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.at.controller.CoreAuditTrailConfigController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class CoreAuditTrailConfigControllerImpl
implements CoreAuditTrailConfigController {
    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u914d\u7f6e\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/at-configs/core-at-config-edit-list";
    }

    @Override
    @RequestMapping(value={"/record-detail"})
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String recordDetailPage() {
        return "module/sys/at-configs/core-at-record-detail";
    }
}
