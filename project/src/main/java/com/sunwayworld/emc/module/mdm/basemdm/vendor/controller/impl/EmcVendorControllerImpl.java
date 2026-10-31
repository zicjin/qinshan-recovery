package com.sunwayworld.emc.module.mdm.basemdm.vendor.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.controller.EmcVendorController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcVendorControllerImpl implements EmcVendorController{

    @Log(value = "生产商供应商编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/vendors/emc-vendor-edit-list";
    }

    @Override
    @Log(value = "生产商供应商选择列表页", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/basemdm/vendors/emc-vendor-choose-list";
    }
}
