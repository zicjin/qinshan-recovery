package com.sunwayworld.emc.module.mdm.basemdm.fixedasset.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.controller.EmcFixedassetController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-25
 */

@Controller
@GikamBean
public class EmcFixedassetControllerImpl implements EmcFixedassetController{

    @Log(value = "固定资产管理表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/fixedassets/emc-fixedasset-edit-list";
    }

    @Log(value = "固定资产管理表详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/basemdm/fixedassets/emc-fixedasset-detail";
    }
    
    @Log(value = "固定资产管理表选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/fixedassets/emc-fixedasset-choose-list";
    }

}
