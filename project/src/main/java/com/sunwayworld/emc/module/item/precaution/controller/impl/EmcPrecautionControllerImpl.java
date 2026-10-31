package com.sunwayworld.emc.module.item.precaution.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.precaution.controller.EmcPrecautionController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

@Controller
@GikamBean
public class EmcPrecautionControllerImpl implements EmcPrecautionController{

    @Log(value = "预防措施计划申请列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/precautions/emc-precaution-edit-list";
    }

    @Override
    @Log(value = "预防措施计划审核列表页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/item/precautions/emc-precaution-audit-list";
    }

    @Override
    @Log(value = "预防措施计划查询列表页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/precautions/emc-precaution-search-list";
    }
}
