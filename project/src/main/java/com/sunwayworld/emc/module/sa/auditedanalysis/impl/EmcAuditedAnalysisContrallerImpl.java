package com.sunwayworld.emc.module.sa.auditedanalysis.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.sa.auditedanalysis.EmcAuditedAnalysisContraller;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcAuditedAnalysisContrallerImpl implements EmcAuditedAnalysisContraller{

    @Override
    @Log(value = "已审核分析数据页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/sa/audited-analysiss/emc-audited-analysis-edit-list";
    }

    @Override
    @Log(value = "已审核分析数据批详情页面", type = LogType.CONTROLLER)
    public String auditAnalysisRunDetailPage() {
        return "emc/module/sa/audited-analysiss/emc-audited-analysis-run-detail-list";
    }

}
