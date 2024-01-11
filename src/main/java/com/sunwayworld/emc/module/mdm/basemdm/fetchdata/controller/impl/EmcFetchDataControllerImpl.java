package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.controller.EmcFetchDataController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@Controller
@GikamBean
public class EmcFetchDataControllerImpl implements EmcFetchDataController {

    @Override
    @Log(value = "生物查表编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/fetch-datas/emc-fetch-data-edit-list";
    }

}
