package com.sunwayworld.emc.module.bp.edit.projectchange.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.edit.projectchange.controller.EmcProjectChangeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年10月17日
 */
@Controller
@GikamBean
public class EmcProjectChangeControllerImpl implements EmcProjectChangeController {

    @Override
    @Log(value = "项目变更编制列表页", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/edit/project-changes/emc-project-change-edit-list";
    }

    @Override
    @Log(value = "项目变更审核列表页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/bp/edit/project-changes/emc-project-change-audit-list";
    }

    @Override
    @Log(value = "项目变更查询列表页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/edit/project-changes/emc-project-change-search-list";
    }
}
