package com.sunwayworld.emc.module.mdm.basemdm.room.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
* @author zhengxy@sunwayworld.com 2020年07月03日
*/
@RequestMapping("/secure/emc/module/mdm/basemdm/rooms/page")
public interface EmcRoomController extends GenericEditListPageController, GenericChoosePageController {
}
