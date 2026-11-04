/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.log.controller.CoreExportLogController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreExportLogControllerImpl
implements CoreExportLogController {
    @Override
    @Log(value="\u5bfc\u51fa\u65e5\u5fd7\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/export-logs/core-export-log-edit-list";
    }
}
