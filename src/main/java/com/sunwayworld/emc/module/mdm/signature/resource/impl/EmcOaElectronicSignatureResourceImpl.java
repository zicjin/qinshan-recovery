package com.sunwayworld.emc.module.mdm.signature.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.signature.resource.EmcOaElectronicSignatureResource;
import com.sunwayworld.emc.module.mdm.signature.service.EmcOaElectronicSignatureService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年12月30日
*/
@GikamBean
@RestController
@LogModule(value = "OA电子签名")
public class EmcOaElectronicSignatureResourceImpl implements EmcOaElectronicSignatureResource {

    @Autowired
    private EmcOaElectronicSignatureService emcOaElectronicSignatureService;

    @Override
    public EmcOaElectronicSignatureService getService() {
        return emcOaElectronicSignatureService;
    }

}
