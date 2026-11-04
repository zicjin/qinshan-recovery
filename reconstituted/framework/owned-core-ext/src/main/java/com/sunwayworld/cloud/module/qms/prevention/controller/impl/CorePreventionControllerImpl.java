/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.prevention.controller.impl;

import com.sunwayworld.cloud.module.qms.prevention.controller.CorePreventionController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u9884\u9632\u63aa\u65bd")
@Controller
@GikamBean
public class CorePreventionControllerImpl
implements CorePreventionController {
    @Override
    @Log(value="\u9884\u9632\u63aa\u65bd\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/preventions/core-prevention-edit-list";
    }

    @Override
    @Log(value="\u9884\u9632\u63aa\u65bd\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/preventions/core-prevention-detail";
    }

    @Override
    @Log(value="\u9884\u9632\u63aa\u65bd\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/preventions/core-prevention-audit-list";
    }

    @Override
    @Log(value="\u9884\u9632\u63aa\u65bd\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/preventions/core-prevention-search-list";
    }
}
