/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.controller.CoreBpmnTargetController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreBpmnTargetControllerImpl
implements CoreBpmnTargetController {
    @Override
    @Log(value="\u6d41\u7a0b\u5bf9\u5e94\u7684\u4e1a\u52a1\u4fe1\u606f\u5f02\u5e38\u5904\u7406\u9875", type=LogType.CONTROLLER)
    public String oddListPage() {
        return "module/sys/bpmn-targets/core-bpmn-target-odd-list";
    }
}
