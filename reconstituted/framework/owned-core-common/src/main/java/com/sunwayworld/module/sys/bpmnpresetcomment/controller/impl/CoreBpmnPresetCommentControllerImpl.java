/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmnpresetcomment.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmnpresetcomment.controller.CoreBpmnPresetCommentController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreBpmnPresetCommentControllerImpl
implements CoreBpmnPresetCommentController {
    @Override
    @Log(value="\u6d41\u7a0b\u9884\u7f6e\u610f\u89c1\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/bpmn-preset-comments/core-bpmn-preset-comment-edit-list";
    }
}
