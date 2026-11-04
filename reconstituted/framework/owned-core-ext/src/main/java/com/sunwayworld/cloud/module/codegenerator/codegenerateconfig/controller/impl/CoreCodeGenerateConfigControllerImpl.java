/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.controller.impl;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.controller.CoreCodeGenerateConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u4ee3\u7801\u751f\u6210\u5668")
@Controller
@GikamBean
public class CoreCodeGenerateConfigControllerImpl
implements CoreCodeGenerateConfigController {
    @Override
    @Log(value="\u4ee3\u7801\u751f\u6210\u5668\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/codegenerator/code-generate-configs/core-code-generate-config-edit-list";
    }

    @Override
    @Log(value="\u4ee3\u7801\u751f\u6210\u5668\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/codegenerator/code-generate-configs/core-code-generate-config-detail";
    }
}
