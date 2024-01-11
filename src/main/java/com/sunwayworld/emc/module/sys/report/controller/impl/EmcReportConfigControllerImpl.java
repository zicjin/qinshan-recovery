package com.sunwayworld.emc.module.sys.report.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.cloud.gikam.fr.controller.report.impl.CoreReportConfigControllerImpl;
import com.sunwayworld.emc.module.sys.report.controller.EmcReportConfigController;

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
}
