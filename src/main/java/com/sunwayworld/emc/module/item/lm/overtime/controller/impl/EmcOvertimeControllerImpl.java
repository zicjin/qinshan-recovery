package com.sunwayworld.emc.module.item.lm.overtime.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.lm.overtime.controller.EmcOvertimeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-14
 */

@Controller
@GikamBean
public class EmcOvertimeControllerImpl implements EmcOvertimeController {

    @Log(value = "加班表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/lm/overtimes/emc-overtime-edit-list";
    }

    @Log(value = "加班表详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/item/lm/overtimes/emc-overtime-detail";
    }

    @Log(value = "加班表选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/item/lm/overtimes/emc-overtime-choose-list";
    }

    @Log(value = "加班表审批详情页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        // TODO Auto-generated method stub
        return "emc/module/item/lm/overtimes/emc-overtime-audit-list";
    }

    @Log(value = "加吧申请查询", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        // TODO Auto-generated method stub
        return "emc/module/item/lm/overtimes/emc-overtime-search-list";
    }
}
