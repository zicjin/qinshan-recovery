package com.sunwayworld.emc.module.mdm.bizmdm.capacity.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.controller.PfTestCapacityController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class PfTestCapacityControllerImpl extends EmcTestCapacityControllerImpl implements PfTestCapacityController {

    @Log(value = "监测能力库方法参数设置页", type = LogType.CONTROLLER)
    @Override
    public String testCapacityMethodParamDetailPage() {
        return "emc/module/mdm/bizmdm/test-capacitys/emc-method-param-edit-test-capacity-list";
    }

    @Log(value = "监测能力库录入方法参数批录入页", type = LogType.CONTROLLER)
    @Override
    public String testCapacityUpdateParamBatchPage() {
        return "emc/module/mdm/bizmdm/test-capacitys/emc-test-capacity-batch-update-param-list";
    }

    @Override
    @Log(value = "监测能力库-分析项组选择页", type = LogType.CONTROLLER)
    public String capacityItemGroupChoosePage() {
        return "emc/module/mdm/bizmdm/test-capacitys/emc-test-capacity-item-group-choose-list";
    }
    
    @Override
    @Log(value = "监测能力库-测试选择页", type = LogType.CONTROLLER)
    public String testCapacityChoosePage() {
        return "emc/module/mdm/bizmdm/test-capacitys/emc-test-capacity-choose-list";
    }

}
