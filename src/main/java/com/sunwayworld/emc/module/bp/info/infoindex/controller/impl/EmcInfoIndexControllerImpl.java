package com.sunwayworld.emc.module.bp.info.infoindex.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.info.infoindex.controller.EmcInfoIndexController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */

@Controller
@GikamBean
public class EmcInfoIndexControllerImpl implements EmcInfoIndexController {

    @Log(value = "评价标准表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/info/info-indexs/emc-info-index-edit-list";
    }

	@Override
	public String choosePage() {
		return "emc/module/bp/info/info-indexs/emc-info-index-choose-list";
	}
}
