package com.sunwayworld.emc.module.mdm.basemdm.area.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.area.controller.EmcAreaController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */

@Controller
@GikamBean
public class EmcAreaControllerImpl implements EmcAreaController{

    @Log(value = "区域表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/areas/emc-area-edit-list";
    }

    @Log(value = "区域表详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/basemdm/areas/emc-area-detail";
    }
    
    @Log(value = "区域表选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/areas/emc-area-choose-list";
    }

}
