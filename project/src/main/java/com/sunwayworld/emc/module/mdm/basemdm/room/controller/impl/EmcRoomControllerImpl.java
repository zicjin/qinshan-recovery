package com.sunwayworld.emc.module.mdm.basemdm.room.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.room.controller.EmcRoomController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年07月03日
*/
@Controller
@GikamBean
public class EmcRoomControllerImpl implements EmcRoomController {

    @Override
    @Log(value = "房间管理编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/rooms/emc-room-edit-list";
    }

    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/rooms/emc-room-choose-list";
    }
}
