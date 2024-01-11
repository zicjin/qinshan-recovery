package com.sunwayworld.emc.module.bp.project.projectlog.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.bp.project.projectlog.controller.EmcProjectEditLogController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcProjectEditLogControllerImpl implements EmcProjectEditLogController {

    @Log(value = "客服方案修改记录表查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/bp/project/project-edit-logs/emc-project-edit-log-search-list";
    }

}
