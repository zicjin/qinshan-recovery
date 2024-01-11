package com.sunwayworld.emc.module.mdm.contract.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.contract.resource.EmcSplitContractResource;
import com.sunwayworld.emc.module.mdm.contract.service.EmcSplitContractService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年11月07日
 */
@LogModule("拆分子合同")
@RestController
@GikamBean
public class EmcSplitContractResourceImpl implements EmcSplitContractResource {

    @Autowired
    private EmcSplitContractService splitContractService;

    @Override
    public EmcSplitContractService getService() {
        return splitContractService;
    }
}
