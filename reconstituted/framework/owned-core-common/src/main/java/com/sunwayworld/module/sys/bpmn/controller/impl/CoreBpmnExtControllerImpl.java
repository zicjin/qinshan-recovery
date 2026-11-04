/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.controller.CoreBpmnExtController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreBpmnExtControllerImpl
implements CoreBpmnExtController {
    @Override
    @Log(value="\u4e1a\u52a1\u6d41\u7a0b\u72b6\u6001\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String processStatusDetailPage() {
        return "module/sys/bpmn-exts/core-bpmn-process-status-detail";
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u8fd0\u884c\u4e2d\u7684\u4e1a\u52a1\u4fe1\u606f\u67e5\u8be2\u9875", type=LogType.CONTROLLER)
    public String runtimeTargetList() {
        return "module/sys/bpmn-exts/core-bpmn-runtime-target-list";
    }
}
