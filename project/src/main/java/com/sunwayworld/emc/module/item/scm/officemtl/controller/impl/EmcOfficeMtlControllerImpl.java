package com.sunwayworld.emc.module.item.scm.officemtl.controller.impl;


import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.scm.officemtl.controller.EmcOfficeMtlController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;


@Controller
@GikamBean
public class EmcOfficeMtlControllerImpl implements EmcOfficeMtlController{

    @Log(value = "办公用品编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/scm/office-mtls/emc-office-mtl-edit-list";
    }

    @Log(value = "办公用品详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/item/scm/office-mtls/emc-office-mtl-detail";
    }


    @Log(value = "办公用品验收页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/scm/office-mtls/emc-office-mtl-check-list";
    }

}
