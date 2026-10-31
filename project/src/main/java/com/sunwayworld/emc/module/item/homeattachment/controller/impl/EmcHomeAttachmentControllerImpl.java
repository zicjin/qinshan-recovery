package com.sunwayworld.emc.module.item.homeattachment.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.homeattachment.controller.EmcHomeAttachmentController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcHomeAttachmentControllerImpl implements EmcHomeAttachmentController {

    @Log(value = "附件信息表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/home-attachments/emc-home-attachment-edit-list";
    }

    @Override
    @Log(value = "批量修改到期日期页", type = LogType.CONTROLLER)
    public String chooseDateDetailPage() {
        return "emc/module/item/home-attachments/emc-home-attachment-choose-date-detail";
    }

}
