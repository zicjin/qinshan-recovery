package com.sunwayworld.emc.module.item.complaint.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.complaint.controller.EmcComplaintController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-13
 */

@Controller
@GikamBean
public class EmcComplaintControllerImpl implements EmcComplaintController {

    @Log(value = "申投诉新增列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/complaints/emc-complaint-edit-list";
    }

    @Log(value = "申投诉审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/complaints/emc-complaint-audit-list";
    }

    @Log(value = "申投诉查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/item/complaints/emc-complaint-search-list";
    }
}
