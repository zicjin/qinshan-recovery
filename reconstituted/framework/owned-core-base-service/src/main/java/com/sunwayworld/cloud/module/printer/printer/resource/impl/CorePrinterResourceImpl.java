/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printer.resource.impl;

import com.sunwayworld.cloud.module.printer.printer.resource.CorePrinterResource;
import com.sunwayworld.cloud.module.printer.printer.service.CorePrinterService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6253\u5370")
@RestController
@GikamBean
public class CorePrinterResourceImpl
implements CorePrinterResource {
    @Autowired
    private CorePrinterService printerService;

    public CorePrinterService getService() {
        return this.printerService;
    }

    @Override
    @Log(value="\u6253\u5370\u6587\u4ef6", type=LogType.PRINT)
    public void print(RestJsonWrapperBean wrapper) {
        this.printerService.print(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u6240\u6709\u6253\u5370\u673a\u540d\u79f0", type=LogType.SELECT)
    public List<String> selectPrinterNameList() {
        return this.printerService.selectPrinterNameList();
    }

    @Override
    @Log(value="\u67e5\u8be2\u652f\u6301\u7684\u7eb8\u5f20\u7c7b\u578b\u540d\u79f0", type=LogType.SELECT)
    public List<String> selectPaperNameList() {
        return this.printerService.selectPaperNameList();
    }
}
