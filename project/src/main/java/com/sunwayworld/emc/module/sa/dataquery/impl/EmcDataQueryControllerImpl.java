package com.sunwayworld.emc.module.sa.dataquery.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.sa.dataquery.EmcDataQueryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
@Controller
@GikamBean
public class EmcDataQueryControllerImpl implements EmcDataQueryController{

    @Override
    @Log(value = "数据查询页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/sa/data-queries/emc-data-query-edit-list";
    }

}
