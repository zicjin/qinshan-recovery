package com.sunwayworld.emc.module.mdm.user.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.controller.impl.CoreUserControllerImpl;

/**
 * 系统用户Controller
 *
 *
 */
@GikamBean
@Controller
public class EmcUserControllerImpl extends CoreUserControllerImpl {

    @Override
    @Log(value = "用户详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/users/emc-user-detail";
    }

    @Override
    @Log(value = "用户列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/users/emc-user-edit-list";
    }

    @Log(value = "系统用户穿梭框页面", type = LogType.CONTROLLER)
    @RequestMapping("/shuttle-frame")
    public String shuttleFramePage() {
        return "emc/module/mdm/users/emc-user-shuttle-frame-edit-list";
    }

}
