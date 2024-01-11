package com.sunwayworld.emc.module.item.scm.standardmtl.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.scm.standardmtl.controller.EmcStandardMtlController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcStandardMtlControllerImpl implements EmcStandardMtlController {

    @Log(value = "标准物质编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/scm/standard-mtls/emc-standard-mtl-edit-list";
    }

    @Log(value = "标准物质验收页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/scm/standard-mtls/emc-standard-mtl-check-list";
    }

    @Log(value = "标准物质详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/item/scm/standard-mtls/emc-standard-mtl-detail";
    }

}
