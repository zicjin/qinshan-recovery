package com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.controller.EmcReportInfoReferenceController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2020年03月06日
 */
@Controller
@GikamBean
public class EmcReportInfoReferenceControllerImpl implements EmcReportInfoReferenceController {

    @Override
    @Log(value = "报告信息对照编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/report-info-references/emc-report-info-reference-edit-list";
    }

}
