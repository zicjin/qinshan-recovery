package com.sunwayworld.emc.module.bp.edit.runedit.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.edit.runedit.EmcRunEditController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
@Controller
@GikamBean
public class EmcRunEditControllerImpl implements EmcRunEditController {

    @Override
    @Log(value = "分析数据修改页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/edit/run-edits/emc-run-edit-edit-list";
    }
    
    @Override
    @Log(value = "按条件查询", type = LogType.CONTROLLER)
    public String queryByCondPage() {
        return "emc/module/bp/edit/run-edits/emc-run-edit-query-list";
    }

    @Override
    @Log(value = "退回报告修改批", type = LogType.CONTROLLER)
    public String editUndoList() {
        return "emc/module/bp/edit/run-edits/emc-undo-run-edit-list";
    }
    
    @Override
    @Log(value = "批结果查询页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/edit/run-edits/emc-run-edit-result-search-list";
    }
    
    @Override
    @Log(value = "条件查询批结果", type = LogType.CONTROLLER)
    public String runResultQueryByCondPage() {
        return "emc/module/bp/edit/run-edits/emc-run-edit-result-query-list";
    }

}
