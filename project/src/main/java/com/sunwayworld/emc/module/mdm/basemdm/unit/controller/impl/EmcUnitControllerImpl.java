package com.sunwayworld.emc.module.mdm.basemdm.unit.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.unit.controller.EmcUnitController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月9日
 */

@Controller
@GikamBean
public class EmcUnitControllerImpl implements EmcUnitController {
    @Override
    @Log(value = "计量单位列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/units/emc-unit-edit-list";
    }

    @Override
    @Log(value = "计量单位详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/basemdm/units/emc-unit-detail";
    }

    @Log(value = "计量单位选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/units/emc-unit-choose-list";
    }
}
