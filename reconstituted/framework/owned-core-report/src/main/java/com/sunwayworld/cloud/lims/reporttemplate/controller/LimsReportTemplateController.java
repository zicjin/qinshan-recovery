/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/lims/module/report-templates/page"})
public interface LimsReportTemplateController
extends GenericEditListPageController,
GenericChoosePageController {
    public String sourceDetailPage();
}
