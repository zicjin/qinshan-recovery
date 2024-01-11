package com.sunwayworld.emc.module.item.inconformity.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.inconformity.controller.EmcInconformityController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-12-6
 */

@Controller
@GikamBean
public class EmcInconformityControllerImpl implements EmcInconformityController{

    @Log(value = "内审计划申请列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/inconformitys/emc-inconformity-edit-list";
    }

    @Override
    @Log(value = "内审计划审核列表页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/item/inconformitys/emc-inconformity-audit-list";
    }
    
    @Override
    @Log(value = "内审计划查询列表页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/inconformitys/emc-inconformity-search-list";
    }
}
