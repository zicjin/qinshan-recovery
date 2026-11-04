/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.controller.impl;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.controller.CorePdfAcqConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="PDF\u91c7\u96c6\u914d\u7f6e")
@GikamBean
@Controller
public class CorePdfAcqConfigControllerImpl
implements CorePdfAcqConfigController {
    @Override
    @Log(value="PDF\u91c7\u96c6\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/equipt/pdf-acq-configs/core-pdf-acq-config-edit-list";
    }

    @Override
    @Log(value="PDF\u91c7\u96c6\u914d\u7f6e\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/equipt/pdf-acq-configs/core-pdf-acq-config-choose-list";
    }
}
