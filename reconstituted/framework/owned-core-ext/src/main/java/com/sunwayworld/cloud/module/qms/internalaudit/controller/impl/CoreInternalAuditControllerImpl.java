/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.controller.impl;

import com.sunwayworld.cloud.module.qms.internalaudit.controller.CoreInternalAuditController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u5185\u90e8\u5ba1\u6838")
@Controller
@GikamBean
public class CoreInternalAuditControllerImpl
implements CoreInternalAuditController {
    @Override
    @Log(value="\u5185\u90e8\u5ba1\u6838\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/internal-audits/core-internal-audit-edit-list";
    }

    @Override
    @Log(value="\u5185\u90e8\u5ba1\u6838\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/internal-audits/core-internal-audit-detail";
    }

    @Override
    @Log(value="\u5185\u90e8\u5ba1\u6838\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/internal-audits/core-internal-audit-audit-list";
    }

    @Override
    @Log(value="\u5185\u90e8\u5ba1\u6838\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/internal-audits/core-internal-audit-search-list";
    }

    @Override
    @Log(value="\u5185\u90e8\u5ba1\u6838\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/internal-audits/core-internal-audit-choose-list";
    }
}
