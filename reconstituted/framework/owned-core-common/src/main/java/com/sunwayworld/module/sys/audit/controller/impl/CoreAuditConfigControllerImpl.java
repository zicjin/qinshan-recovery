/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.audit.controller.CoreAuditConfigController;
import org.springframework.stereotype.Controller;

@GikamBean
@Controller
public class CoreAuditConfigControllerImpl
implements CoreAuditConfigController {
    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u914d\u7f6e\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/audit/audit-configs/core-audit-config-edit-list";
    }
}
