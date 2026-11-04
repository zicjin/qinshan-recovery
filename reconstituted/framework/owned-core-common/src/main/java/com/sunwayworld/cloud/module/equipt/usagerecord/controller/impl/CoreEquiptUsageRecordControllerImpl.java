/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.usagerecord.controller.impl;

import com.sunwayworld.cloud.module.equipt.usagerecord.controller.CoreEquiptUsageRecordController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u8bbe\u5907\u4f7f\u7528\u8bb0\u5f55")
@Controller
@GikamBean
public class CoreEquiptUsageRecordControllerImpl
implements CoreEquiptUsageRecordController {
    @Override
    @Log(value="\u8bbe\u5907\u4f7f\u7528\u8bb0\u5f55\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/equipt/usage-records/core-equipt-usage-record-search-list";
    }

    @Override
    @Log(value="\u8bbe\u5907\u4f7f\u7528\u7387\u9875", type=LogType.CONTROLLER)
    public String equiptUsageRatePage() {
        return "module/equipt/usage-records/core-equipt-usage-rate";
    }

    @Override
    @Log(value="\u8bbe\u5907\u8d21\u732e\u503c\u5206\u6790\u9875", type=LogType.CONTROLLER)
    public String equiptContributionAnalysisPage() {
        return "module/equipt/usage-records/core-equipt-contribution-analysis";
    }

    @Override
    @Log(value="\u603b\u4f53\u4f7f\u7528\u7387\u70b9\u51fb\u67e5\u770b\u8bbe\u5907\u4f7f\u7528\u7387\u660e\u7ec6\u9875", type=LogType.CONTROLLER)
    public String equiptMonthUsageRateSearchListPage() {
        return "module/equipt/usage-records/core-equipt-month-usage-rate-search-list";
    }

    @Override
    @Log(value="\u8bbe\u5907\u8d21\u732e\u503c\u6708\u5ea6\u8bb0\u5f55\u9875", type=LogType.CONTROLLER)
    public String equiptMonthContributionSearchListPage() {
        return "module/equipt/usage-records/core-equipt-month-contribution-search-list";
    }
}
