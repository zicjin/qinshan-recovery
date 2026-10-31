package com.sunwayworld.emc.module.mdm.basemdm.indexitem.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.controller.EmcIndexItemController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */

@Controller
@GikamBean
public class EmcIndexItemControllerImpl implements EmcIndexItemController {

    @Log(value = "指标项目编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/index-items/emc-index-item-edit-list";
    }

    @Log(value = "指标项目选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/index-items/emc-index-item-choose-list";

    }

}
