package com.sunwayworld.emc.module.item.scm.officemtl.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.scm.officemtl.resource.EmcOfficeMtlResource;
import com.sunwayworld.emc.module.item.scm.officemtl.service.EmcOfficeMtlService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("办公用品")
@RestController
@GikamBean
public class EmcOfficeMtlResourceImpl implements EmcOfficeMtlResource {
    @Autowired
    private EmcOfficeMtlService officeMtlService;
    @Override
    public EmcOfficeMtlService getService() {
        return officeMtlService;
    }


    @Log(value = "新增办公用品", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
