/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.controller;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/annual-plans/page"})
public interface CoreAnnualPlanController
extends GenericEditListPageController,
GenericDetailPageController,
GenericAuditListPageController,
GenericSearchListPageController,
GenericChoosePageController {
    @RequestMapping(value={"/emp-train-detail"})
    public String empTrainDetailPage();

    @RequestMapping(value={"/daily-supervision-detail"})
    public String dailySupervisionDetailPage();

    @RequestMapping(value={"/equ-func-check-detail"})
    public String equFuncCheckDetailPage();

    @RequestMapping(value={"/internal-audit-detail"})
    public String internalAuditDetailPage();

    @RequestMapping(value={"/manage-review-detail"})
    public String manageReviewDetailPage();

    @RequestMapping(value={"/period-verify-detail"})
    public String periodVerifyDetailPage();

    @RequestMapping(value={"/quality-control-detail"})
    public String qualityControlDetailPage();

    @RequestMapping(value={"/verify-calibrate-detail"})
    public String verifyCalibrateDetailPage();
}
