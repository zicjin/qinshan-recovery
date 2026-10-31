package com.sunwayworld.emc.module.mdm.basemdm.cert.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.cert.controller.EmcCertController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

@Controller
@GikamBean
public class EmcCertControllerImpl implements EmcCertController{

    @Log(value = "人员证书表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/certs/emc-cert-edit-list";
    }

    @Log(value = "人员证书表详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/basemdm/certs/emc-cert-detail";
    }
    
    @Log(value = "人员证书表选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/certs/emc-cert-choose-list";
    }

}
