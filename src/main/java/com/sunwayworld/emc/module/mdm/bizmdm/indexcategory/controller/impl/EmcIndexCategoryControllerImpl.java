package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.controller.EmcIndexCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcIndexCategoryControllerImpl implements EmcIndexCategoryController {

    @Override
    @Log(value = "指标分类列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/bizmdm/index-categories/emc-index-category-edit-list";
    }

    @Override
    @Log(value = "指标分类详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/bizmdm/index-categories/emc-index-category-detail";
    }

    @Log(value = "样品指标选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/bizmdm/index-categories/emc-order-index-choose-list";
    }

    @Log(value = "样品指标详情页(监测点管理)", type = LogType.CONTROLLER)
    @Override
    public String orderIndexDetailPage() {
        return "emc/module/mdm/bizmdm/index-categories/emc-order-index-detail";
    }

    @Log(value = "指标预览", type = LogType.CONTROLLER)
    @Override
    public String indexPreviewListPage() {
        return "emc/module/mdm/bizmdm/index-categories/emc-index-preview-list";
    }

}
