package com.sunwayworld.emc.module.bp.info.infoconfig.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.info.infoconfig.controller.EmcInfoConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcInfoConfigControllerImpl implements EmcInfoConfigController {

    @Log(value = "综合信息配置编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/info/info-configs/emc-info-config-edit-list";
    }

    @Override
    public String choosePage() {
        return "emc/module/bp/info/info-configs/emc-info-config-choose-list";
    }

}
