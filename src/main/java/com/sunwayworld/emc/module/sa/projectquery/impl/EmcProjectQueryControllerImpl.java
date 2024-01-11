package com.sunwayworld.emc.module.sa.projectquery.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.sa.projectquery.EmcProjectQueryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
@Controller
@GikamBean
public class EmcProjectQueryControllerImpl implements EmcProjectQueryController {

    @Override
    @Log(value = "项目查询页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/sa/project-queries/emc-project-query-edit-list";
    }

    @Override
    @Log(value = "按条件查询", type = LogType.CONTROLLER)
    public String queryByCondPage() {
        return "emc/module/sa/project-queries/emc-project-query-cond-query-list";
    }
}
