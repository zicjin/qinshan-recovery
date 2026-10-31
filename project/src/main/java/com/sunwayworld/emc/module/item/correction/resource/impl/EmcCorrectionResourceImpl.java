package com.sunwayworld.emc.module.item.correction.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.correction.resource.EmcCorrectionResource;
import com.sunwayworld.emc.module.item.correction.service.EmcCorrectionService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

@LogModule("纠正措施计划")
@RestController
@GikamBean
public class EmcCorrectionResourceImpl implements EmcCorrectionResource {
    @Autowired
    private EmcCorrectionService correctionService;

    @Override
    public EmcCorrectionService getService() {
        return correctionService;
    }
}
