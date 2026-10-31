package com.sunwayworld.emc.module.mdm.bizmdm.method.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.bizmdm.method.controller.EmcMethodController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

@Controller
@GikamBean
public class EmcMethodControllerImpl implements EmcMethodController {

    @Log(value = "方法编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/bizmdm/methods/emc-method-edit-list";
    }

    @Log(value = "方法详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/bizmdm/methods/emc-method-detail";
    }

    @Log(value = "方法选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/bizmdm/methods/emc-method-choose-list";
    }

    @Log(value = "易标准获取方法选择页", type = LogType.CONTROLLER)
    @Override
    public String methodEstandardListPage() {
        return "emc/module/mdm/bizmdm/methods/emc-method-estandard-list";
    }

}
