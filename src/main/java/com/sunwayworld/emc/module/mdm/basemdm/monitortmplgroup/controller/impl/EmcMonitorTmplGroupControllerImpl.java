package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.controller.EmcMonitorTmplGroupController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */

@Controller
@GikamBean
public class EmcMonitorTmplGroupControllerImpl implements EmcMonitorTmplGroupController {

    @Log(value = "监测模板组编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/monitor-tmpl-groups/emc-monitor-tmpl-group-edit-list";
    }

    @Log(value = "监测模板组选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/monitor-tmpl-groups/emc-monitor-tmpl-group-choose-list";
    }

}
