package com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.controller.EmcEquiptRequisitionController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcEquiptRequisitionControllerImpl implements EmcEquiptRequisitionController {

    @Log(value = "仪器出入库编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/equipt-requisitions/emc-equipt-requisition-edit-list";
    }

    @Log(value = "仪器出入库审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/mdm/basemdm/equipt-requisitions/emc-equipt-requisition-audit-list";
    }

    @Log(value = "仪器出入库查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/mdm/basemdm/equipt-requisitions/emc-equipt-requisition-search-list";
    }

    @Log(value = "仪器领用详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/basemdm/equipt-requisitions/emc-equipt-requisition-detail";
    }

    @Log(value = "仪器出库编制列表页", type = LogType.CONTROLLER)
    @RequestMapping(value = "/instrument-out")
    public String instrumentOutListPage() {
        return "emc/module/mdm/basemdm/equipt-requisitions/emc-instrument-out-edit-list";
    }

}
