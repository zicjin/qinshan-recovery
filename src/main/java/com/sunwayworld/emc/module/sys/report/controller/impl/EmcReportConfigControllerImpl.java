package com.sunwayworld.emc.module.sys.report.controller.impl;

import com.sunwayworld.cloud.module.fr.report.controller.impl.CoreReportConfigControllerImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.sys.report.controller.EmcReportConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

/**
 * @author zhengxy@sunwayworld.com 2019年11月04日
 */
@Controller
public class EmcReportConfigControllerImpl extends CoreReportConfigControllerImpl implements EmcReportConfigController {

    @Override
    public String detailPage() {
        return "emc/module/sys/reports/emc-report-config-detail";
    }

    @Override
    public String editListPage() {
        return "emc/module/sys/reports/emc-report-config-edit-list";
    }

    @Override
    public String lineSelectPage() {
        return "emc/module/sys/reports/emc-report-config-line-select";
    }

    @Override
    public String previewPage() {
        return "emc/module/sys/reports/emc-report-preview";
    }
    
    @Override
    public String lineChooseListPage() {
        return "emc/module/sys/reports/emc-report-config-line-choose-list";
    }

    @Log(value = "报表通用页", type = LogType.CONTROLLER)
    @RequestMapping(value = "/common-report")
    public String commonReportPage() {
        return "emc/module/sys/reports/emc-report-common-list";
    }
}
