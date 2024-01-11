package com.sunwayworld.emc.module.index.homepage.homepage.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.index.homepage.controller.impl.CoreHomepageControllerImpl;

@Controller
@GikamBean
public class EmcHomepageControllerImpl extends CoreHomepageControllerImpl {
    @Override
    @Log(value = "首页", type = LogType.CONTROLLER)
    public String indexPage() {
        return "emc/module/index/homepage/index";
    }
}
