package com.sunwayworld.emc.module.bp.result.olifilm.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.bp.result.olifilm.controller.EmcOilFilmController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcOilFilmControllerImpl implements EmcOilFilmController {

    @Log(value = "油膜滤筒信息表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/result/oil-films/emc-oil-film-edit-list";
    }

}
