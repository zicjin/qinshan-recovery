package com.sunwayworld.emc.module.mdm.signature.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.signature.controller.EmcOaElectronicSignatureController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年12月30日
*/
@Controller
@GikamBean
public class EmcOaElectronicSignatureControllerImpl implements EmcOaElectronicSignatureController {

    @Override
    @Log(value = "OA电子签名编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/signatures/emc-oa-electronic-signature-edit-list";
    }

    @Override
    @Log(value = "OA电子签名审核列表页面", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/mdm/signatures/emc-oa-electronic-signature-audit-list";
    }

    @Override
    @Log(value = "OA电子签名查询列表页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/mdm/signatures/emc-oa-electronic-signature-search-list";
    }
}
