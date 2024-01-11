package com.sunwayworld.emc.module.mdm.basemdm.testcategory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.testcategory.controller.EmcTestCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcTestCategoryControllerImpl implements EmcTestCategoryController {

    @Override
    @Log(value = "测试类型编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/test-categories/emc-test-category-edit-list";
    }

    @Override
    @Log(value = "测试类型详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/basemdm/test-categories/emc-test-category-detail";
    }

    @Override
    @Log(value = "测试类型弹出选择页面", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/basemdm/test-categories/emc-test-category-choose-list";
    }
}
