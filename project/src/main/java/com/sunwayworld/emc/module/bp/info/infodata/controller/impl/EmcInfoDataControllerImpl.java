package com.sunwayworld.emc.module.bp.info.infodata.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.info.infodata.controller.EmcInfoDataController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcInfoDataControllerImpl implements EmcInfoDataController {

    @Log(value = "综合信息编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/info/info-datas/emc-info-data-edit-list";
    }

}
