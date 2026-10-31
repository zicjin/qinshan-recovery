package com.sunwayworld.emc.module.item.qualitycontrol.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.qualitycontrol.controller.EmcQualityControlController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcQualityControlControllerImpl implements EmcQualityControlController{

    @Override
    @Log(value = "质量控制计划列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/item/quality-controls/emc-quality-control-edit-list";
    }

    @Override
    @Log(value = "质量控制计划审核页面", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/item/quality-controls/emc-quality-control-audit-list";
    }

    @Override
    @Log(value = "质量控制计划查询页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/quality-controls/emc-quality-control-search-list";
    }

}
