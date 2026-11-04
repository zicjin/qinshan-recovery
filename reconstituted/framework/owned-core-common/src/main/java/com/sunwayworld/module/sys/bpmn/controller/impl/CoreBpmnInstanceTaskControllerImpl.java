/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.controller.CoreBpmnInstanceTaskController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreBpmnInstanceTaskControllerImpl
implements CoreBpmnInstanceTaskController {
    @Override
    @Log(value="\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b\u7684\u4efb\u52a1\u7f16\u8f91\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/bpmn-inst-tasks/core-bpmn-inst-task-edit-list";
    }
}
