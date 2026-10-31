package com.sunwayworld.emc.module.mdm.basemdm.room.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.room.resource.EmcRoomResource;
import com.sunwayworld.emc.module.mdm.basemdm.room.service.EmcRoomService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年07月03日
*/
@GikamBean
@RestController
@LogModule(value = "房间管理")
public class EmcRoomResourceImpl implements EmcRoomResource {

    @Autowired
    private EmcRoomService emcRoomService;

    @Override
    public EmcRoomService getService() {
        return emcRoomService;
    }

}
