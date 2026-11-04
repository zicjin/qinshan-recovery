/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.controller.impl;

import com.sunwayworld.cloud.module.qms.externalaudit.controller.CoreExternalAuditController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u5916\u90e8\u8bc4\u5ba1")
@Controller
@GikamBean
public class CoreExternalAuditControllerImpl
implements CoreExternalAuditController {
    @Override
    @Log(value="\u5916\u90e8\u8bc4\u5ba1\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/external-audits/core-external-audit-edit-list";
    }

    @Override
    @Log(value="\u5916\u90e8\u8bc4\u5ba1\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/external-audits/core-external-audit-detail";
    }

    @Override
    @Log(value="\u5916\u90e8\u8bc4\u5ba1\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/external-audits/core-external-audit-audit-list";
    }

    @Override
    @Log(value="\u5916\u90e8\u8bc4\u5ba1\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/external-audits/core-external-audit-search-list";
    }

    @Override
    @Log(value="\u5916\u90e8\u8bc4\u5ba1\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/external-audits/core-external-audit-choose-list";
    }
}
