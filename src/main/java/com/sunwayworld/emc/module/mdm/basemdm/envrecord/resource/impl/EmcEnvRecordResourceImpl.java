package com.sunwayworld.emc.module.mdm.basemdm.envrecord.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.envrecord.resource.EmcEnvRecordResource;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.service.EmcEnvRecordService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "环境记录")
@RestController
@GikamBean
public class EmcEnvRecordResourceImpl implements EmcEnvRecordResource {

    @Autowired
    private EmcEnvRecordService envRecordService;

    @Override
    public EmcEnvRecordService getService() {
        return envRecordService;
    }

    @Log(value = "新增环境记录", type = LogType.INSERT)
    @Override
    public String insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
