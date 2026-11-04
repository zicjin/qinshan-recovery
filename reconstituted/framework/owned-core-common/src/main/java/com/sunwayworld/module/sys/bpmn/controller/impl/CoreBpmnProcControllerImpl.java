/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.controller.CoreBpmnProcController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreBpmnProcControllerImpl
implements CoreBpmnProcController {
    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/sys/bpmn-procs/core-bpmn-proc-detail";
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u67e5\u8be2\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/sys/bpmn-procs/core-bpmn-proc-search-list";
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/sys/bpmn-procs/core-bpmn-proc-choose-list";
    }
}
