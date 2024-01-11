package com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equipt.resource.PfEquiptPerCheckResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.service.EmcEquiptPerCheckService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;


@RestController
@GikamBean
public class PfEquiptPerCheckResourceImpl implements PfEquiptPerCheckResource {

    @Autowired
    private EmcEquiptPerCheckService equiptPerCheckService;

    @Override
    public EmcEquiptPerCheckService getService() {
        return equiptPerCheckService;
    }

    @Log(value = "新增仪器期间核查", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
