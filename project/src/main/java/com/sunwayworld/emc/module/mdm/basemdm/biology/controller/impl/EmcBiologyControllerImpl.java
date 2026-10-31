package com.sunwayworld.emc.module.mdm.basemdm.biology.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.biology.controller.EmcBiologyController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-28
 */

@Controller
@GikamBean
public class EmcBiologyControllerImpl implements EmcBiologyController{

    @Log(value = "生物项编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/biologys/emc-biology-edit-list";
    }

    @Log(value = "生物项详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/basemdm/biologys/emc-biology-detail";
    }
    
    @Log(value = "生物项选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/biologys/emc-biology-choose-list";
    }

}
