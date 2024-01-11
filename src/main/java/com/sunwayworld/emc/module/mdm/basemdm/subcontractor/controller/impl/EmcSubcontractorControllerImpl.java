package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.controller.EmcSubcontractorController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */

@Controller
@GikamBean
public class EmcSubcontractorControllerImpl implements EmcSubcontractorController {

    @Log(value = "分包商编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/subcontractors/emc-subcontractor-edit-list";
    }

    @Log(value = "分包商选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/subcontractors/emc-subcontractor-choose-list";
    }

}
