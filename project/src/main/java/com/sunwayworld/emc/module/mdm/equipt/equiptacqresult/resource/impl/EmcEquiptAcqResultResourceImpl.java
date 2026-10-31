package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.resource.EmcEquiptAcqResultResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service.EmcEquiptAcqResultService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "文件采集结果")
@RestController
@GikamBean
public class EmcEquiptAcqResultResourceImpl implements EmcEquiptAcqResultResource {

    @Autowired
    private EmcEquiptAcqResultService equiptAcqResultService;

    @Override
    public EmcEquiptAcqResultService getService() {
        return equiptAcqResultService;
    }

}
