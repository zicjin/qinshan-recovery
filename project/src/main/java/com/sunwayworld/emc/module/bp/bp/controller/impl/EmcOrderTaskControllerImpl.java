package com.sunwayworld.emc.module.bp.bp.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.bp.controller.EmcOrderTaskController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcOrderTaskControllerImpl implements EmcOrderTaskController{

    @Override
    public String editListPage() {
        return null;
    }

    @Log(value = "样品测试选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/bp/folders/emc-order-task-choose-list";
    }
    
    @Override
    @Log(value = "QC类型弹出选择页面-分析录入", type = LogType.CONTROLLER)
    public String resultRunChoosePage() {
        return "emc/module/bp/result/runs/emc-run-qc-category-choose-list";
    }

    @Override
    @Log(value = "样品测试查看图谱页面", type = LogType.CONTROLLER)
    public String atlasPage() {
        return "emc/module/bp/folders/emc-order-task-atlas";
    }

    @Override
    @Log(value = "修改结果原因填写页面", type = LogType.CONTROLLER)
    public String updateResultReasonPage() {
        return "emc/module/bp/folders/emc-update-result-reason";

    }

}
