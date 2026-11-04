/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.result.controller.impl;

import com.sunwayworld.cloud.module.script.result.controller.CoreResultController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u7ed3\u679c\u5f55\u5165")
@Controller
@GikamBean
public class CoreResultControllerImpl
implements CoreResultController {
    @Override
    @Log(value="\u7ed3\u679c\u5f55\u5165\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/script/results/core-result-edit-list";
    }
}
