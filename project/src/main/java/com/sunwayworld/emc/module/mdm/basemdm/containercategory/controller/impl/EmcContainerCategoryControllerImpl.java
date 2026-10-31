package com.sunwayworld.emc.module.mdm.basemdm.containercategory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.containercategory.controller.EmcContainerCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcContainerCategoryControllerImpl implements EmcContainerCategoryController{

    @Override
    @Log(value = "容器类型编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/container-categories/emc-container-category-edit-list";
    }

    @Override
    @Log(value = "容器类型弹出选择页面", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/basemdm/container-categories/emc-container-category-choose-list";
    }
    
    @Override
    @Log(value = "容器维护页面", type = LogType.CONTROLLER)
    public String maintenanceListPage() {
        return "emc/module/mdm/basemdm/container-categories/emc-container-maintenance-list";
    }
    
    @Log(value = "容器维护添加页", type = LogType.CONTROLLER)
    @Override
    public String chooseTMPage() {
        return "emc/module/mdm/basemdm/container-categories/emc-test-method-add-list";
    }
    
    @Log(value = "容器维护左侧复制页", type = LogType.CONTROLLER)
    @Override
    public String copyCMPage() {
        return "emc/module/mdm/basemdm/container-categories/emc-container-maintenance-copy-form";
    }
    
    @Log(value = "方法测试选择页", type = LogType.CONTROLLER)
    @Override
    public String chooseTestMethodPage() {
        return "emc/module/mdm/basemdm/container-categories/emc-container-maintenance-choose-tm-list";
    }
    
    @Log(value = "容器维护选择页", type = LogType.CONTROLLER)
    @Override
    public String chooseContainerPage() {
        return "emc/module/mdm/basemdm/container-categories/emc-container-choose-list";
    }
    
    @Log(value = "容器维护修改页", type = LogType.CONTROLLER)
    @Override
    public String modifyContainerPage() {
        return "emc/module/mdm/basemdm/container-categories/emc-container-modify-form";
    }
}
