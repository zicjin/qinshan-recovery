/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.applycomplaint.controller.impl;

import com.sunwayworld.cloud.module.qms.applycomplaint.controller.CoreApplyComplaintController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u7533\u6295\u8bc9")
@Controller
@GikamBean
public class CoreApplyComplaintControllerImpl
implements CoreApplyComplaintController {
    @Override
    @Log(value="\u7533\u6295\u8bc9\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/apply-complaints/core-apply-complaint-edit-list";
    }

    @Override
    @Log(value="\u7533\u6295\u8bc9\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/apply-complaints/core-apply-complaint-detail";
    }

    @Override
    @Log(value="\u7533\u6295\u8bc9\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/apply-complaints/core-apply-complaint-audit-list";
    }

    @Override
    @Log(value="\u7533\u6295\u8bc9\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/apply-complaints/core-apply-complaint-search-list";
    }
}
