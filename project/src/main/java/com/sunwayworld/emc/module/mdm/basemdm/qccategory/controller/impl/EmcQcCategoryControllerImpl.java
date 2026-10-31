package com.sunwayworld.emc.module.mdm.basemdm.qccategory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.qccategory.controller.EmcQcCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcQcCategoryControllerImpl implements EmcQcCategoryController {

    @Override
    @Log(value = "QC类型编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/qc-categories/emc-qc-category-edit-list";
    }

    @Override
    @Log(value = "QC类型弹出选择页面", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/basemdm/qc-categories/emc-qc-category-choose-list";
    }

    @Override
    @Log(value = "QC类型弹出选择页面简单版", type = LogType.CONTROLLER)
    public String simpleChoosePage() {
        return "emc/module/mdm/basemdm/qc-categories/emc-qc-category-simple-choose-list";
    }

}
