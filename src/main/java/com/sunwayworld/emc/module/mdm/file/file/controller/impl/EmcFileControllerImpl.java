package com.sunwayworld.emc.module.mdm.file.file.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.file.file.controller.EmcFileController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Controller
@GikamBean
public class EmcFileControllerImpl implements EmcFileController {

    @Override
    @Log(value = "文件管理编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/file/files/emc-file-edit-list";
    }

    @Override
    @Log(value = "文件管理详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/file/files/emc-file-detail";
    }

    @Override
    @Log(value = "文件查询页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/mdm/file/files/emc-file-search-list";
    }

    @Override
    @Log(value = "跳转至文件预览页面", type = LogType.CONTROLLER)
    @RequestMapping("/view")
    public String viewPage() {
        return "emc/module/mdm/file/files/emc-file-view-list";
    }

    @Override
    @Log(value = "跳转至文件预览页面", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/file/files/emc-file-choose-list";
    }
}
