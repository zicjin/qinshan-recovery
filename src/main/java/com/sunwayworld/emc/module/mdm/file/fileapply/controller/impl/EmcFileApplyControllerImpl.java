package com.sunwayworld.emc.module.mdm.file.fileapply.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.file.fileapply.controller.EmcFileApplyController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcFileApplyControllerImpl implements EmcFileApplyController {

    @Override
    @Log(value = "文件申请编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/file/file-applies/emc-file-apply-edit-list";
    }

    @Override
    @Log(value = "文件申请审核列表页面", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/mdm/file/file-applies/emc-file-apply-audit-list";
    }

    @Override
    @Log(value = "申请文件查看页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/mdm/file/file-applies/emc-file-apply-search-list";
    }

}
