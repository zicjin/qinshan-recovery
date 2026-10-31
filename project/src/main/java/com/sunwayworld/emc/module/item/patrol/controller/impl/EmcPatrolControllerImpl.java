package com.sunwayworld.emc.module.item.patrol.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.patrol.controller.EmcPatrolController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-09
 */

@Controller
@GikamBean
public class EmcPatrolControllerImpl implements EmcPatrolController{

    @Log(value = "巡查月计划编制列表页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/scheme/edit-list")
    public String schemeEditListPage() {
        return "emc/module/item/patrols/scheme/emc-patrol-scheme-edit-list";
    }
    
    @Log(value = "巡查记录编制列表页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/record/edit-list")
    public String recordEditListPage() {
        return "emc/module/item/patrols/record/emc-patrol-record-edit-list";
    }
    
    @Log(value = "巡查处理编制列表页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/handle/edit-list")
    public String handleEditListPage() {
        return "emc/module/item/patrols/handle/emc-patrol-handle-edit-list";
    }

 
	@Override
	public String editListPage() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public String choosePage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String detailPage() {
        // TODO Auto-generated method stub
        return null;
    }

}
