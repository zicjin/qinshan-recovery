/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.usagerecord.controller;

import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/equipt/usage-records/page"})
public interface CoreEquiptUsageRecordController
extends GenericSearchListPageController {
    @RequestMapping(value={"/usage-rate"})
    public String equiptUsageRatePage();

    @RequestMapping(value={"/contribution-analysis"})
    public String equiptContributionAnalysisPage();

    @RequestMapping(value={"/month-usage-rate-search-list"})
    public String equiptMonthUsageRateSearchListPage();

    @RequestMapping(value={"/month-contribution-search-list"})
    public String equiptMonthContributionSearchListPage();
}
