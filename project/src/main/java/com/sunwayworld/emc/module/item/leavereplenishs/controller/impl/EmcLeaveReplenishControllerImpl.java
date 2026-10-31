package com.sunwayworld.emc.module.item.leavereplenishs.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.leavereplenishs.controller.EmcLeaveReplenishController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-14
 */

@Controller
@GikamBean
public class EmcLeaveReplenishControllerImpl implements EmcLeaveReplenishController{

    @Log(value = "补休假编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/leave-replenishs/emc-leave-replenish-edit-list";
    }


    @Override
    @Log(value = "补休假审核列表页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/item/leave-replenishs/emc-leave-replenish-audit-list";
    }
    
    @Override
    @Log(value = "补休假查询列表页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/leave-replenishs/emc-leave-replenish-search-list";
    }

}
