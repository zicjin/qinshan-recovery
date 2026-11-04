/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/report-configs/page"})
public interface CoreReportConfigController
extends GenericEditListPageController,
GenericDetailPageController,
GenericChoosePageController {
    @RequestMapping(value={"/line-select"})
    public String lineSelectPage();

    @RequestMapping(value={"/line-choose-list"})
    public String lineChooseListPage();

    @RequestMapping(value={"/line-search-list"})
    public String lineSearchListPage();

    @RequestMapping(value={"/sub-report-edit-list"})
    public String subReportEditListPage();
}
