package com.sunwayworld.emc.module.item.scm.standardmtl.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.scm.standardmtl.resource.EmcStandardMtlResource;
import com.sunwayworld.emc.module.item.scm.standardmtl.service.EmcStandardMtlService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("标准物质")
@RestController
@GikamBean
public class EmcStandardMtlResourceImpl implements EmcStandardMtlResource {
    @Autowired
    private EmcStandardMtlService emcStandardMtlService;
    @Override
    public EmcStandardMtlService getService() {
        return emcStandardMtlService;
    }


    @Log(value = "新增标准物质", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
