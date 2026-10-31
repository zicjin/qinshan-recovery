package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.controller.EmcPropertyTmplController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
public class EmcPropertyTmplControllerImpl implements EmcPropertyTmplController {

    @Override
    @Log(value = "属性设置编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/property-tmpl/emc-property-tmpl-edit-list";
    }

    @Override
    @Log(value = "属性设置详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/basemdm/property-tmpl/emc-property-tmpl-detail";
    }
    
    @Log(value = "属性字段详情页", type = LogType.CONTROLLER)
    @Override
    public String fieldDetailPage() {
        return "emc/module/mdm/basemdm/property-tmpl/emc-property-field-detail";
    }

    @Log(value = "属性字段弹出选择页面", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/property-tmpl/emc-property-tmpl-choose-list";
    }

}
