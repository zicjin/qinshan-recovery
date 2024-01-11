package com.sunwayworld.emc.module.mdm.bizmdm.client.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.bizmdm.client.controller.EmcClientController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@Controller
@GikamBean
public class EmcClientControllerImpl implements EmcClientController {

    @Log(value = "客户编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/bizmdm/clients/emc-client-edit-list";
    }

    @Log(value = "客户详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/bizmdm/clients/emc-client-detail";
    }

    @Log(value = "客户选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/bizmdm/clients/emc-client-choose-list";
    }

    @Log(value = "监测点选择页", type = LogType.CONTROLLER)
    @Override
    public String monitorPointchoosePage() {
        return "emc/module/mdm/bizmdm/clients/emc-monitor-point-choose-list";
    }

    @Log(value = "监测点简单选择页", type = LogType.CONTROLLER)
    @Override
    public String monitorPointSimpleChoosePage() {
        return "emc/module/mdm/bizmdm/clients/emc-monitor-point-simple-choose-list";
    }
    
    @Log(value = "监测点编制列表页", type = LogType.CONTROLLER)
    @Override
    public String monitorPointEditPage() {
        return "emc/module/mdm/bizmdm/clients/emc-monitor-point-edit-list";
    }

    @Log(value = "客户联系人选择页", type = LogType.CONTROLLER)
    @Override
    public String contactChoosePage() {
        return "emc/module/mdm/bizmdm/clients/emc-client-contact-choose-list";
    }

    @Override
    public String subClientChoosePage() {
        return "emc/module/mdm/bizmdm/clients/emc-client-sub-choose-list";
    }
}
