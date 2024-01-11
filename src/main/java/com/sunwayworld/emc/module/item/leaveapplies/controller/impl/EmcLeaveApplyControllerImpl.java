package com.sunwayworld.emc.module.item.leaveapplies.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.leaveapplies.controller.EmcLeaveApplyController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-12
 */

@Controller
@GikamBean
public class EmcLeaveApplyControllerImpl implements EmcLeaveApplyController{

    @Log(value = "日常请假申请列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/leave-applies/emc-leave-apply-edit-list";
    }

    @Override
    @Log(value = "日常请假审核列表页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/item/leave-applies/emc-leave-apply-audit-list";
    }

    @Override
    @Log(value = "日常请假查询列表页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/leave-applies/emc-leave-apply-search-list";
    }

    @Log(value = "日常请假详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/item/leave-applies/emc-leave-apply-detail";
    }
}
