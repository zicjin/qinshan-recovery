package com.sunwayworld.emc.module.item.sql.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.sql.controller.EmcSqlController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcSqlControllerImpl implements EmcSqlController {

    @Log(value = "sql管理编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/sqls/emc-sql-edit-list";
    }

}
