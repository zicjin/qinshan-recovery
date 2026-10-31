package com.sunwayworld.emc.module.bp.edit.runedit;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
/**
 * 分析数据修改Controller
 * 
 */
@RequestMapping("/secure/emc/module/bp/edit/run-edits/page")
public interface EmcRunEditController extends GenericEditListPageController , GenericSearchListPageController{

    @RequestMapping("/cond/query")
    String queryByCondPage();

    @RequestMapping("/undo-run-edit")
    String editUndoList();
    
    @RequestMapping("/run-result/cond/query")
    String runResultQueryByCondPage();
}
