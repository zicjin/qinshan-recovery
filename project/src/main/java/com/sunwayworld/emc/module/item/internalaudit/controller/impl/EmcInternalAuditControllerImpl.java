package com.sunwayworld.emc.module.item.internalaudit.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.internalaudit.controller.EmcInternalAuditController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

@Controller
@GikamBean
public class EmcInternalAuditControllerImpl implements EmcInternalAuditController{

    @Log(value = "内审计划申请列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/internal-audits/emc-internal-audit-edit-list";
    }

    @Override
    @Log(value = "内审计划审核列表页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/item/internal-audits/emc-internal-audit-audit-list";
    }
    
    @Override
    @Log(value = "内审计划查询列表页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/internal-audits/emc-internal-audit-search-list";
    }
}
