package com.sunwayworld.emc.module.mdm.filemodification.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.filemodification.controller.EmcFileModificationController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhanghy@sunwayworld.com 2020年09月01日
 */
@Controller
@GikamBean
public class EmcFileModificationControllerImpl implements EmcFileModificationController {

    @Override
    @Log(value = "文件修改申请页", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/file-modifications/emc-file-modification-edit-list";
    }
    
    @Override
    @Log(value = "文件修改审核页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/mdm/file-modifications/emc-file-modification-audit-list";
    }
    
    @Override
    @Log(value = "文件修改查询页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/mdm/file-modifications/emc-file-modification-search-list";
    }
	
    @Override
    @Log(value = "文件选择页", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/file-modifications/emc-file-modification-choose-list";
    }
}
