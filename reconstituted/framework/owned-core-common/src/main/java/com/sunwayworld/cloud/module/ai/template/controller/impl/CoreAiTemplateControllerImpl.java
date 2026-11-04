/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.controller.impl;

import com.sunwayworld.cloud.module.ai.template.controller.CoreAiTemplateController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="AI\u6a21\u677f\u7ba1\u7406")
@Controller
@GikamBean
public class CoreAiTemplateControllerImpl
implements CoreAiTemplateController {
    @Override
    @Log(value="\u6a21\u677f\u8868\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/ai/templates/core-ai-template-edit-list";
    }

    @Override
    @Log(value="\u6a21\u677f\u8868\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/ai/templates/core-ai-template-detail";
    }
}
