package com.sunwayworld.emc.module.bp.project.projscheme.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.project.projscheme.controller.EmcProjSchemeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

@Controller
@GikamBean
public class EmcProjSchemeControllerImpl implements EmcProjSchemeController {

    @Log(value = "监测方案表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/project/proj-schemes/emc-proj-scheme-edit-list";
    }
    
    @Log(value = "选择监测点页", type = LogType.CONTROLLER)
    @Override
    public String selectMonitoringPointPage() {
        return "emc/module/bp/project/proj-schemes/emc-select-monitoring-point";
    }
    
    @Log(value = "选择检测模板页", type = LogType.CONTROLLER)
    @Override
    public String selectMonitorTmplPage() {
        return "emc/module/bp/project/proj-schemes/emc-select-monitor-tmpl";
    }

    @Log(value = "选择检测模板页报价单", type = LogType.CONTROLLER)
    @Override
    public String selectMonitorTmplDetectionPage() {
        return "emc/module/bp/project/proj-schemes/emc-select-monitor-tmpl-detection";
    }

    @Log(value = "复制份页", type = LogType.CONTROLLER)
    @Override
    public String copySharePage() {
        return "emc/module/bp/project/proj-schemes/emc-copy-share";
    }
    
    @Log(value = "修改周期频次页", type = LogType.CONTROLLER)
    @Override
    public String updateCycleAndFrequencyPage() {
        return "emc/module/bp/project/proj-schemes/emc-update-cycle-and-frequency";
    }
    
    @Log(value = "批修改个数页", type = LogType.CONTROLLER)
    @Override
    public String batchUpdatePage() {
        return "emc/module/bp/project/proj-schemes/emc-proj-order-batch-update";
    }
    
    @Log(value = "评价标准页", type = LogType.CONTROLLER)
    @Override
    public String updateIndexPage() {
        return "emc/module/bp/project/proj-schemes/emc-update-index";
    }
    
    @Log(value = "编辑测试页", type = LogType.CONTROLLER)
    @Override
    public String updateItemGroup() {
        return "emc/module/bp/project/proj-schemes/emc-proj-order-update-item-group";
    }

    @Log(value = "初始化方案", type = LogType.CONTROLLER)
    @Override
    public String initScheme() {
        return "emc/module/bp/project/proj-schemes/emc-proj-init-scheme";
    }

}
