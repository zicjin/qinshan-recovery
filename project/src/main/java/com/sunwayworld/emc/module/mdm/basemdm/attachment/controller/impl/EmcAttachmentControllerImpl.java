package com.sunwayworld.emc.module.mdm.basemdm.attachment.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.attachment.controller.EmcAttachmentController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcAttachmentControllerImpl implements EmcAttachmentController {

    @Log(value = "附件编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/attachments/emc-attachment-edit-list";
    }

}
