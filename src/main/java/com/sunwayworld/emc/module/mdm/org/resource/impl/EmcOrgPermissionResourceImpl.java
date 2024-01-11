package com.sunwayworld.emc.module.mdm.org.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.org.resource.EmcOrgPermissionResource;
import com.sunwayworld.emc.module.mdm.org.service.EmcOrgPermissionService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "单位权限")
@RestController
@GikamBean
public class EmcOrgPermissionResourceImpl implements EmcOrgPermissionResource {

    @Autowired
    private EmcOrgPermissionService orgPermissionService;

    @Override
    public EmcOrgPermissionService getService() {
        return orgPermissionService;
    }

    @Override
    @Log(value = "新增单位权限", type = LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
