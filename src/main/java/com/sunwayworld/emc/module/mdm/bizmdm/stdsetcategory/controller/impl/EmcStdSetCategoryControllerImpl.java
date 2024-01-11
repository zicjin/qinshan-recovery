package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.controller.EmcStdSetCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcStdSetCategoryControllerImpl implements EmcStdSetCategoryController {

    @Log(value = "标准设置分类编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/bizmdm/std-set-categorys/emc-std-set-category-edit-list";
    }

    @Log(value = "字段设置选择字段信息页", type = LogType.CONTROLLER)
    @Override
    public String chooseFieldInfoPage() {
        return "emc/module/mdm/bizmdm/std-set-categorys/emc-field-info-choose-list";
    }

    @Log(value = "标准设置公式页", type = LogType.CONTROLLER)
    @Override
    public String standSetScriptDetailPage() {
        return "emc/module/mdm/bizmdm/std-set-categorys/emc-stand-set-script-detail";
    }

}
