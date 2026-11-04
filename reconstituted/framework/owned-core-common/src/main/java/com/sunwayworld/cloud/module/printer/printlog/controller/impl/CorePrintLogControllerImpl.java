/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printlog.controller.impl;

import com.sunwayworld.cloud.module.printer.printlog.controller.CorePrintLogController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CorePrintLogControllerImpl
implements CorePrintLogController {
    @Override
    @Log(value="\u6253\u5370\u8bb0\u5f55\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/printer/print-logs/core-printer-print-log-search-list";
    }
}
