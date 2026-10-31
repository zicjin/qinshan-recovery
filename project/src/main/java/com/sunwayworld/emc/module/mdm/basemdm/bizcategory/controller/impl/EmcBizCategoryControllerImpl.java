package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.controller.EmcBizCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcBizCategoryControllerImpl implements EmcBizCategoryController {

    @Override
    @Log(value = "业务类型编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/biz-categories/emc-biz-category-edit-list";
    }

    @Override
    @Log(value = "业务类型详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/basemdm/biz-categories/emc-biz-category-detail";
    }

    @Log(value = "表单样式选择页面", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/biz-categories/emc-biz-category-choose-list";
    }

    @Log(value = "登记科室编辑页面", type = LogType.CONTROLLER)
    @Override
    public String orgPermissionEditPage() {
        return "emc/module/mdm/basemdm/biz-categories/emc-biz-category-org-permission-edit-list";
    }
    
    @Override
    @Log(value = "业务类型简单选择页面", type = LogType.CONTROLLER)
    public String simpleChoosePage() {
        return "emc/module/mdm/basemdm/biz-categories/emc-biz-category-simple-choose-list";
    }

    @Override
    @Log(value = "业务类型常用项目选择页面", type = LogType.CONTROLLER)
    public String projectChoosePage() {
        return "emc/module/mdm/basemdm/biz-categories/emc-biz-category-project-choose-list";
    }

}
