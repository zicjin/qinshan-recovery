package com.sunwayworld.emc.module.mdm.basemdm.script.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.script.resource.EmcScriptResource;
import com.sunwayworld.emc.module.mdm.basemdm.script.service.EmcScriptService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("计算公式")
@RestController
@GikamBean
public class EmcScriptResourceImpl implements EmcScriptResource {
    @Autowired
    private EmcScriptService emcScriptService;

    @Override
    public EmcScriptService getService() {
        return emcScriptService;
    }

    @Log(value = "新增计算公式", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public String insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
