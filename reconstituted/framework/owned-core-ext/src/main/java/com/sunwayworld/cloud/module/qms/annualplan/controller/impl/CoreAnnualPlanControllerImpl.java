/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.controller.impl;

import com.sunwayworld.cloud.module.qms.annualplan.controller.CoreAnnualPlanController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u5e74\u5ea6\u8ba1\u5212")
@Controller
@GikamBean
public class CoreAnnualPlanControllerImpl
implements CoreAnnualPlanController {
    @Override
    @Log(value="\u5e74\u5ea6\u8ba1\u5212\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/annual-plans/core-annual-plan-edit-list";
    }

    @Override
    @Log(value="\u5e74\u5ea6\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/annual-plans/core-annual-plan-detail";
    }

    @Override
    @Log(value="\u5e74\u5ea6\u8ba1\u5212\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/annual-plans/core-annual-plan-audit-list";
    }

    @Override
    @Log(value="\u5e74\u5ea6\u8ba1\u5212\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/annual-plans/core-annual-plan-search-list";
    }

    @Override
    @Log(value="\u5e74\u5ea6\u8ba1\u5212\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/annual-plans/core-annual-plan-choose-list";
    }

    @Override
    @Log(value="\u5458\u5de5\u57f9\u8bad\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String empTrainDetailPage() {
        return "module/qms/annual-plan/emp-train-plans/core-emp-train-plan-detail";
    }

    @Override
    @Log(value="\u65e5\u5e38\u76d1\u7763\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String dailySupervisionDetailPage() {
        return "module/qms/annual-plan/daily-supervision-plans/core-daily-supervision-plan-detail";
    }

    @Override
    @Log(value="\u8bbe\u5907\u529f\u80fd\u68c0\u67e5\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String equFuncCheckDetailPage() {
        return "module/qms/annual-plan/equ-func-check-plans/core-equ-func-check-plan-detail";
    }

    @Override
    @Log(value="\u5185\u90e8\u5ba1\u6838\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String internalAuditDetailPage() {
        return "module/qms/annual-plan/internal-audit-plans/core-internal-audit-plan-detail";
    }

    @Override
    @Log(value="\u7ba1\u7406\u8bc4\u5ba1\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String manageReviewDetailPage() {
        return "module/qms/annual-plan/manage-review-plans/core-manage-review-plan-detail";
    }

    @Override
    @Log(value="\u671f\u95f4\u6838\u67e5\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String periodVerifyDetailPage() {
        return "module/qms/annual-plan/period-verify-plans/core-period-verify-plan-detail";
    }

    @Override
    @Log(value="\u68c0\u9a8c\u8d28\u91cf\u63a7\u5236\u8ba1\u5212\u5212\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String qualityControlDetailPage() {
        return "module/qms/annual-plan/quality-control-plans/core-quality-control-plan-detail";
    }

    @Override
    @Log(value="\u4eea\u5668\u8bbe\u5907\u68c0\u5b9a\u6821\u51c6\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String verifyCalibrateDetailPage() {
        return "module/qms/annual-plan/verify-calibrate-plans/core-verify-calibrate-plan-detail";
    }
}
