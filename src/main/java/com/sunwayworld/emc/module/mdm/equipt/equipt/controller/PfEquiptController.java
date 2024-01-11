package com.sunwayworld.emc.module.mdm.equipt.equipt.controller;

import org.springframework.web.bind.annotation.RequestMapping;



/**
 * @author licm@sunwayworld.com 2022年01月24日
 */
public interface PfEquiptController extends EmcEquiptController {

    @RequestMapping("/equipt-plan-annex-list")
    String equiptPlanAnnexList();
    
    @RequestMapping("/equipt-plan-search-list")
    String equiptPlanSearchList();
    
    @RequestMapping("/equipt-pre-check-plan-search-list")
    String equiptPreCheckSearchList();
    
    @RequestMapping("/equipt-maintain-repair-search-list")
    String equiptMaintainRepairList();
    
    @RequestMapping("/equipt-user-search-list")
    String equiptUserSearchList();
    
    @RequestMapping("/equipt-per-check-plan-edit-list")
    String equiptPerCheckPlanList();
    
    @RequestMapping("/equipt-per-check-plan-audit-list")
    String equiptPerCheckPlanAuditList();
    
    @RequestMapping("/equipt-repair-plan-edit-list")
    String equiptRepairPlanList();
    
    @RequestMapping("/equipt-repair-plan-audit-list")
    String equiptRepairPlanAuditList();
}