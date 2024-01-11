package com.sunwayworld.emc.module.mdm.basemdm.filecategory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.filecategory.controller.EmcFileCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月9日
 */
@Controller
@GikamBean
public class EmcFileCategoryControllerImpl implements EmcFileCategoryController {

    @Override
    @Log(value = "文件类型详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/basemdm/file-categories/emc-file-category-detail";
    }

    @Override
    @Log(value = "文件类型编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/file-categories/emc-file-category-edit-list";
    }
}
