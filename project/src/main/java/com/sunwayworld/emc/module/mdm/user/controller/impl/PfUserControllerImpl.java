package com.sunwayworld.emc.module.mdm.user.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年10月12日
 */
@Controller
@GikamBean
public class PfUserControllerImpl extends EmcUserControllerImpl {

    @Log(value = "系统用户选择页面（精简页面）跳转", type = LogType.CONTROLLER)
    @Override
    public String simpleChoosePage() {
        return "emc/module/mdm/users/emc-user-simple-choose-list";
    }
}
