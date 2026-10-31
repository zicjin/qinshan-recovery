package com.sunwayworld.emc.module.item.mtlpercheck.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.mtlpercheck.resource.EmcMtlPerCheckResource;
import com.sunwayworld.emc.module.item.mtlpercheck.service.EmcMtlPerCheckService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("标准物质校准计划")
@RestController
@GikamBean
public class EmcMtlPerCheckResourceImpl implements EmcMtlPerCheckResource {

    @Autowired
    private EmcMtlPerCheckService emcMtlPerCheckService;

    @Override
    public EmcMtlPerCheckService getService() {
        return emcMtlPerCheckService;
    }

    @Log(value = "新增标准物质校准计划", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
