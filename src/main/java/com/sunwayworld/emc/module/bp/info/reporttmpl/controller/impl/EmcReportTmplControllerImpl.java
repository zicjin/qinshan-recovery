package com.sunwayworld.emc.module.bp.info.reporttmpl.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.info.reporttmpl.controller.EmcReportTmplController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcReportTmplControllerImpl implements EmcReportTmplController {

    @Log(value = "例行上报模板表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/info/report-tmpls/emc-report-tmpl-edit-list";
    }

    @Log(value = "例行上报模板表选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/bp/info/report-tmpls/emc-report-tmpl-choose-list";
    }

    @Log(value = "例行上报模板配置表编辑页面", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/line-edit")
    public String lineEditPage() {
        return "emc/module/bp/info/report-tmpls/emc-report-tmpl-line-edit-list";
    }

}
