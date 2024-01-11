package com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.controller.EmcMtlCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-16
 */

@Controller
@GikamBean
public class EmcMtlCategoryControllerImpl implements EmcMtlCategoryController {

    @Log(value = "材料类型编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/mtl-categories/emc-mtl-category-edit-list";
    }

}
