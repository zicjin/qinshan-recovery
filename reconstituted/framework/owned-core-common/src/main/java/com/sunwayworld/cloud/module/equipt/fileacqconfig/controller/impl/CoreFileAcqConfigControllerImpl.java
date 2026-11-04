/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.controller.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.controller.CoreFileAcqConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreFileAcqConfigControllerImpl
implements CoreFileAcqConfigController {
    @Override
    @Log(value="\u6587\u4ef6\u91c7\u96c6\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/equipt/file-acq-configs/core-file-acq-config-edit-list";
    }

    @Override
    @Log(value="\u6587\u4ef6\u91c7\u96c6\u914d\u7f6e\u6d4b\u8bd5\u89e3\u6790\u9875", type=LogType.CONTROLLER)
    public String testAcqResultListPage() {
        return "module/equipt/file-acq-configs/core-file-acq-config-test-acq-result-list";
    }

    @Override
    @Log(value="\u6587\u4ef6\u91c7\u96c6\u914d\u7f6e\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/equipt/file-acq-configs/core-file-acq-config-choose-list";
    }
}
