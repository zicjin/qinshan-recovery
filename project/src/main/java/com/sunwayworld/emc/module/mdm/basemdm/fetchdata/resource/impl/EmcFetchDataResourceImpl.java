package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.resource.EmcFetchDataResource;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service.EmcFetchDataService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@GikamBean
@RestController
@LogModule(value = "生物查表")
public class EmcFetchDataResourceImpl implements EmcFetchDataResource {

    @Autowired
    private EmcFetchDataService emcFetchDataService;

    @Override
    public EmcFetchDataService getService() {
        return emcFetchDataService;
    }

    @Override
    @Log(value = "生成生物查表数据", type = LogType.INSERT)
    @RequestMapping(value = "/action/generate", method = RequestMethod.POST)
    public void generateLine(RestJsonWrapperBean wrapper) {
        emcFetchDataService.generateLine(wrapper);
    }
}
