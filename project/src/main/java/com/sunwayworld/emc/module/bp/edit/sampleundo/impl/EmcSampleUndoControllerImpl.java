package com.sunwayworld.emc.module.bp.edit.sampleundo.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.edit.sampleundo.EmcSampleUndoController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

/**
 * @author zhengxy@sunwayworld.com 2020年11月24日
 */
@Controller
public class EmcSampleUndoControllerImpl implements EmcSampleUndoController {

    @Override
    @Log(value = "样品接收退回页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/edit/sample-undos/emc-sample-undo-edit-list";
    }
}
