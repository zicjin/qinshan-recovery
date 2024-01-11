package com.sunwayworld.emc.module.item.scm.convenmtl.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.scm.convenmtl.resource.EmcConvenMtlResource;
import com.sunwayworld.emc.module.item.scm.convenmtl.service.EmcConvenMtlService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("常规物资")
@RestController
@GikamBean
public class EmcConvenMtlResourceImpl implements EmcConvenMtlResource {
    @Autowired
    private EmcConvenMtlService convenMtlService;
    @Override
    public EmcConvenMtlService getService() {
        return convenMtlService;
    }


    @Log(value = "新增常规物资", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
