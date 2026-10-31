package com.sunwayworld.emc.module.item.scm.convenmtl.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.scm.convenmtl.controller.EmcConvenMtlController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcConvenMtlControllerImpl implements EmcConvenMtlController{

    @Log(value = "常规物资编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/scm/conven-mtls/emc-conven-mtl-edit-list";
    }
    
    @Log(value = "常规物资验收页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/scm/conven-mtls/emc-conven-mtl-check-list";
    }

    @Log(value = "常规物资详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/item/scm/conven-mtls/emc-conven-mtl-detail";
    }


}
