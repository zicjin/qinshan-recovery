package com.sunwayworld.emc.module.item.reportqueries.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.reportqueries.controller.EmcReportQueryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-07
 */

@Controller
@GikamBean
public class EmcReportQueryControllerImpl implements EmcReportQueryController{

    @Override
    @Log(value = "数据报告查用申请列表页", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/item/report-queries/emc-report-query-edit-list";
    }

    @Override
    @Log(value = "数据报告查用审核列表页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/item/report-queries/emc-report-query-audit-list";
    }
    
    @Override
    @Log(value = "数据报告查用查询列表页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/report-queries/emc-report-query-search-list";
    }
}
