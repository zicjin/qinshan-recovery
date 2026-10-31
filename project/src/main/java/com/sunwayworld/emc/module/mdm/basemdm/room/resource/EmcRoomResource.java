package com.sunwayworld.emc.module.mdm.basemdm.room.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.room.bean.EmcRoomBean;
import com.sunwayworld.emc.module.mdm.basemdm.room.service.EmcRoomService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
* @author zhengxy@sunwayworld.com 2020年07月03日
*/
@RequestMapping("/secure/emc/module/mdm/basemdm/rooms")
public interface EmcRoomResource extends GenericResource<EmcRoomService, EmcRoomBean, Long>, GenericChoosableResource<EmcRoomService, EmcRoomBean, Long> {

}
