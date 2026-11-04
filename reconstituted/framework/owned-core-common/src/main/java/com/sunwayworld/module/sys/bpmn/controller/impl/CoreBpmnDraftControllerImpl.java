/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.controller.CoreBpmnDraftController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreBpmnDraftControllerImpl
implements CoreBpmnDraftController {
    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/bpmn-drafts/core-bpmn-draft-edit-list";
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u7f16\u5236\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/sys/bpmn-drafts/core-bpmn-draft-detail";
    }
}
