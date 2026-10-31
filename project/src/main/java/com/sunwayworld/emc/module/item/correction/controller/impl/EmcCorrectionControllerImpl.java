package com.sunwayworld.emc.module.item.correction.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.correction.controller.EmcCorrectionController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

@Controller
@GikamBean
public class EmcCorrectionControllerImpl implements EmcCorrectionController{

    @Log(value = "纠正措施计划申请列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/corrections/emc-correction-edit-list";
    }

    @Override
    @Log(value = "纠正措施计划审核列表页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/item/corrections/emc-correction-audit-list";
    }

    @Override
    @Log(value = "纠正措施计划查询列表页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/corrections/emc-correction-search-list";
    }
}
