package com.sunwayworld.emc.module.item.qualitysupervision.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.qualitysupervision.controller.EmcQualitySupervisionController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@Controller
@GikamBean
public class EmcQualitySupervisionControllerImpl implements EmcQualitySupervisionController {

    @Log(value = "质量监督计划编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/quality-supervisions/emc-quality-supervision-edit-list";
    }

    @Log(value = "质量监督计划审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/quality-supervisions/emc-quality-supervision-audit-list";
    }

    @Override
    @Log(value = "质量监督计划查询列表页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/quality-supervisions/emc-quality-supervision-search-list";
    }
}
