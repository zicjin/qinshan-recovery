package com.sunwayworld.emc.module.mdm.emp.emp.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.emp.emp.controller.EmcEmpController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */

@Controller
@GikamBean
public class EmcEmpControllerImpl implements EmcEmpController {

    @Override
    @Log(value = "人员管理列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/emp/emps/emc-emp-edit-list";
    }

    @Override
    @Log(value = "人员管理详细页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/emp/emps/emc-emp-detail";
    }

    @Override
    @Log(value = "人员管理选择页面", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/emp/emps/emc-emp-choose-list";
    }

    @Override
    @Log(value = "报告编制人员选择页面", type = LogType.CONTROLLER)
    public String reportEmpChoosePage() {
        return "emc/module/mdm/emp/emps/emc-emp-report-choose-list";
    }
    
    @Override
    @Log(value = "采样人员选择页面", type = LogType.CONTROLLER)
    public String sampleEmpChoosePage() {
        return "emc/module/mdm/emp/emps/emc-emp-sample-choose-list";
    }
}
