package com.sunwayworld.emc.module.item.improvement.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.improvement.controller.EmcImprovementController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-09
 */

@Controller
@GikamBean
public class EmcImprovementControllerImpl implements EmcImprovementController {

    @Log(value = "改进措施编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/improvements/emc-improvement-edit-list";
    }

    @Log(value = "改进措施审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/improvements/emc-improvement-audit-list";
    }

    @Log(value = "改进措施查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/item/improvements/emc-improvement-search-list";
    }
}
