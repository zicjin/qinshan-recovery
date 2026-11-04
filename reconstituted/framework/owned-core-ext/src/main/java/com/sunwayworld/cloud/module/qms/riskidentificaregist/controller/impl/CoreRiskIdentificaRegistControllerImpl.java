/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskidentificaregist.controller.impl;

import com.sunwayworld.cloud.module.qms.riskidentificaregist.controller.CoreRiskIdentificaRegistController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u98ce\u9669\u8bc4\u4f30\u4e0e\u63a7\u5236")
@Controller
@GikamBean
public class CoreRiskIdentificaRegistControllerImpl
implements CoreRiskIdentificaRegistController {
    @Override
    @Log(value="\u98ce\u9669\u8bc6\u522b\u767b\u8bb0\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/risk-identifica-regists/core-risk-identifica-regist-detail";
    }

    @Override
    @Log(value="\u98ce\u9669\u8bc6\u522b\u767b\u8bb0\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/risk-identifica-regists/core-risk-identifica-regist-edit-list";
    }

    @Override
    @Log(value="\u98ce\u9669\u8bc4\u4f30\u4e0e\u5904\u7f6e\u5ba1\u6838\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/risk-identifica-regists/core-risk-identifica-regist-audit-list";
    }

    @Override
    @Log(value="\u5e94\u5bf9\u63aa\u65bd\u5b9e\u65bd\u9875", type=LogType.CONTROLLER)
    public String chooseMultPage() {
        return "module/qms/risk-identifica-regists/core-risk-identifica-choose-mult";
    }

    @Override
    @Log(value="\u98ce\u9669\u8bc6\u522b\u767b\u8bb0\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/risk-identifica-regists/core-risk-identifica-regist-choose-list";
    }

    @Override
    @Log(value="\u98ce\u9669\u8bc4\u4f30\u4e0e\u5904\u7f6e\u7f16\u8f91\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String assessEditListPage() {
        return "module/qms/risk-identifica-regists/core-risk-assess-edit-list";
    }

    @Override
    @Log(value="\u5c0f\u7ec4\u8bc4\u5ba1\u9875", type=LogType.CONTROLLER)
    public String assessAuditListPage() {
        return "module/qms/risk-identifica-regists/core-risk-assess-audit-list";
    }

    @Override
    @Log(value="\u5e94\u5bf9\u63aa\u65bd\u6267\u884c\u7f16\u8f91\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String executeEditListPage() {
        return "module/qms/risk-identifica-regists/core-risk-execute-edit-list";
    }

    @Override
    @Log(value="\u5e94\u5bf9\u63aa\u65bd\u6267\u884c\u8bc4\u5ba1\u9875", type=LogType.CONTROLLER)
    public String executeAuditListPage() {
        return "module/qms/risk-identifica-regists/core-risk-execute-audit-list";
    }

    @Override
    @Log(value="\u5f52\u6863\u67e5\u8be2\u5217\u8868\u9875\u9762")
    public String archiveSearchListPage() {
        return "module/qms/risk-identifica-regists/core-risk-archive-search-list";
    }
}
