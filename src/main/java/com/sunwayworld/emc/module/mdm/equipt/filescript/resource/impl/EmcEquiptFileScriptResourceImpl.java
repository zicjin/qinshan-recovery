package com.sunwayworld.emc.module.mdm.equipt.filescript.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.filescript.resource.EmcEquiptFileScriptResource;
import com.sunwayworld.emc.module.mdm.equipt.filescript.service.EmcEquiptFileScriptService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "设备脚本")
@RestController
@GikamBean
public class EmcEquiptFileScriptResourceImpl implements EmcEquiptFileScriptResource {
    @Autowired
    private EmcEquiptFileScriptService equiptFileScriptService;

    @Override
    public EmcEquiptFileScriptService getService() {
        return equiptFileScriptService;
    }

    @Log(value = "新增设备脚本", type = LogType.INSERT)
    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
