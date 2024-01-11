package com.sunwayworld.emc.module.mdm.bizmdm.test.controller.impl;


import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.bizmdm.test.controller.PfTestMethodController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;


@Controller
@GikamBean
public class PfTestMethodControllerImpl extends EmcTestMethodControllerImpl implements PfTestMethodController {
	
    @Log(value = "方法录入方法参数设置页", type = LogType.CONTROLLER)
    @Override
    public String resultOrderTaskMethodParamDetailPage() {
        return "emc/module/mdm/bizmdm/tests/emc-result-order-task-method-param-edit-test-method-list";
    }
    
    @Log(value = "方法录入方法参数批录入页", type = LogType.CONTROLLER)
    @Override
    public String resultOrderTaskMethodParamBatchPage() {
        return "emc/module/mdm/bizmdm/tests/emc-result-order-task-method-param-batch-update-test-method-list";
    }

    @Log(value = "测试管理录入方法参数批录入页", type = LogType.CONTROLLER)
    @Override
    public String taskMethodUpdateParamBatchPage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-method--batch-update-param-list";
    }
    

    @Log(value = "分析录入修改仪器按样品", type = LogType.CONTROLLER)
    @Override
    public String resultOrderTaskMethodParamBatchOrderPage() {
        return "emc/module/mdm/bizmdm/tests/emc-result-order-task-method-param-batch-update-order-list";
    }
    
    @Log(value = "测试方法按照样品类型复制页", type = LogType.CONTROLLER)
    @Override
    public String copyByCategoryPage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-method-copy-by-category";
    }

}
