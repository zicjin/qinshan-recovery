package com.sunwayworld.emc.module.item.inconformity.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.inconformity.resource.EmcInconformityResource;
import com.sunwayworld.emc.module.item.inconformity.service.EmcInconformityService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author cuikx@sunwayworld.com
 * @date 2019-12-6
 */

@LogModule("不符合项登记")
@RestController
@GikamBean
public class EmcInconformityResourceImpl implements EmcInconformityResource {
    @Autowired
    private EmcInconformityService inconformityService;
    @Override
    public EmcInconformityService getService() {
        return inconformityService;
    }
}
