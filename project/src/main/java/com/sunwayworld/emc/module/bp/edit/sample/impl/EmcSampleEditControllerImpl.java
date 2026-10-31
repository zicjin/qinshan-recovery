package com.sunwayworld.emc.module.bp.edit.sample.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.edit.sample.EmcSampleEditController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-11-21
 */

@Controller
public class EmcSampleEditControllerImpl implements EmcSampleEditController{

    @Override
    @Log(value = "现场数据修改页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/edit/sample-edits/emc-sample-edit-list";
    }
    
    @Override
    @Log(value = "按条件查询", type = LogType.CONTROLLER)
    public String queryByCondPage() {
        return "emc/module/bp/edit/sample-edits/emc-sample-edit-query-list";
    }

}
