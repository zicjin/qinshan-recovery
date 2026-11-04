/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.controller.impl;

import com.sunwayworld.cloud.module.les.controller.CoreLesTemplateController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreLesTemplateControllerImpl
implements CoreLesTemplateController {
    @Override
    @Log(value="LES\u6a21\u677f\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/les/les-templates/core-les-template-edit-list";
    }

    @Override
    @Log(value="LES\u6a21\u677f\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/les/les-templates/core-les-template-audit-list";
    }

    @Override
    @Log(value="LES\u6a21\u677f\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/les/les-templates/core-les-template-detail";
    }

    @Override
    @Log(value="LES\u6a21\u677f\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/les/les-templates/core-les-template-choose-list";
    }

    @Override
    @Log(value="SpreadSheet\u9875\u9762", type=LogType.CONTROLLER)
    public String spreadSheetPage() {
        return "module/les/les-templates/core-les-template-spread-sheet";
    }

    @Override
    @Log(value="LES\u7ec4\u4ef6\u4e2d\u5316\u5b66\u5206\u5b50\u5f0f\u9875\u9762", type=LogType.CONTROLLER)
    public String chemDoodlePage() {
        return "module/les/les-templates/core-les-template-chem-doodle-web";
    }
}
