package com.sunwayworld.emc.module.mdm.org.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.module.mdm.org.controller.impl.CoreOrgControllerImpl;

/**
 * EMC系统组织机构Controller
 *
 */
@Controller
public class EmcOrgControllerImpl extends CoreOrgControllerImpl {

    @Override
    @Log(value = "组织结构详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/orgs/emc-org-detail";
    }

    @Override
    @Log(value = "组织结构列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/orgs/emc-org-edit-list";
    }

    @Log(value = "组织结构选择页面", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/orgs/emc-org-choose-list";
    }

    @Log(value = "组织结构选择下拉", type = LogType.CONTROLLER)
    @RequestMapping("/select-list")
    public String selectPage() {
        return "emc/module/mdm/orgs/emc-org-select-list";
    }

}
