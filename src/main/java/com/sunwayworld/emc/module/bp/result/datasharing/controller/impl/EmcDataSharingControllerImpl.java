package com.sunwayworld.emc.module.bp.result.datasharing.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.result.datasharing.controller.EmcDataSharingController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2020-02-10
 * 
 */
@Controller
@GikamBean
public class EmcDataSharingControllerImpl implements EmcDataSharingController {

    @Override
    @Log(value = "数据共享页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/result/data-sharings/emc-data-sharing-edit-list";
    }

}
