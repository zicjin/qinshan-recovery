package com.sunwayworld.emc.module.item.precaution.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.precaution.resource.EmcPrecautionResource;
import com.sunwayworld.emc.module.item.precaution.service.EmcPrecautionService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

@LogModule("预防措施计划")
@RestController
@GikamBean
public class EmcPrecautionResourceImpl implements EmcPrecautionResource {
    @Autowired
    private EmcPrecautionService precautionService;

    @Override
    public EmcPrecautionService getService() {
        return precautionService;
    }
}
