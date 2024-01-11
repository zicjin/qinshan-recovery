package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.resource.EmcFetchDataLineResource;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service.EmcFetchDataLineService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@GikamBean
@RestController
@LogModule(value = "生物查表明细")
public class EmcFetchDataLineResourceImpl implements EmcFetchDataLineResource {

    @Autowired
    private EmcFetchDataLineService emcFetchDataLineService;

    @Override
    public EmcFetchDataLineService getService() {
        return emcFetchDataLineService;
    }

}
