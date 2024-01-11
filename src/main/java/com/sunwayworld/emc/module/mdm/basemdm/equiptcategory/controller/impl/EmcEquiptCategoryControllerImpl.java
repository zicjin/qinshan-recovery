package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.controller.EmcEquiptCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com 2019年9月4日
 */
@Controller
@GikamBean
public class EmcEquiptCategoryControllerImpl implements EmcEquiptCategoryController {

    @Override
    @Log(value = "仪器类型编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/equipt-categories/emc-equipt-category-edit-list";
    }

}
