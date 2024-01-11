package com.sunwayworld.emc.module.mdm.equipt.equipt.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.equipt.controller.PfEquiptController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;


@Controller
@GikamBean
public class PfEquiptControllerImpl extends EmcEquiptControllerImpl implements PfEquiptController {

    
    @Override
    @Log(value = "仪器计划上传附件页面", type = LogType.CONTROLLER)
    public String equiptPlanAnnexList() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-plan-annex-list";
    }
    
    @Override
    @Log(value = "仪器检定计划查询", type = LogType.CONTROLLER)
    public String equiptPlanSearchList() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-plan-search-list";
    }
    
    @Override
    @Log(value = "仪器期间核查台账查询", type = LogType.CONTROLLER)
    public String equiptPreCheckSearchList() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-pre-check-plan-search-list";
    }
    
    @Override
    @Log(value = "仪器维修查询", type = LogType.CONTROLLER)
    public String equiptMaintainRepairList() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-maintain-repair-search-list";
    }
    
    @Override
    @Log(value = "仪器设备使用授权人员清单", type = LogType.CONTROLLER)
    public String equiptUserSearchList() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-user-search-list";
    }
    
    @Override
    @Log(value = "仪器期间核查计划", type = LogType.CONTROLLER)
    public String equiptPerCheckPlanList() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-per-check-plan-edit-list";
    }
    
    @Override
    @Log(value = "仪器期间核查记录", type = LogType.CONTROLLER)
    public String equiptPerCheckPlanAuditList() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-per-check-plan-audit-list";
    }
     
    @Override
    @Log(value = "仪器维修计划", type = LogType.CONTROLLER)
    public String equiptRepairPlanList() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-repair-plan-edit-list";
    }
    
    @Override
    @Log(value = "仪器维修计划记录", type = LogType.CONTROLLER)
    public String equiptRepairPlanAuditList() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-repair-plan-audit-list";
    }
}