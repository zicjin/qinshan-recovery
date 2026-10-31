package com.sunwayworld.emc.module.item.managereview.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.managereview.controller.EmcManageReviewController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcManageReviewControllerImpl implements EmcManageReviewController {

    @Log(value = "管理评审计划编制页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/manage-reviews/emc-manage-review-edit-list";
    }

    @Log(value = "管理评审计划审核页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/manage-reviews/emc-manage-review-audit-list";
    }

    @Log(value = "管理评审计划查询页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/item/manage-reviews/emc-manage-review-search-list";
    }

}
