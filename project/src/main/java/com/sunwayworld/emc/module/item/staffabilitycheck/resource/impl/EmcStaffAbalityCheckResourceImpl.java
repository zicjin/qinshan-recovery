package com.sunwayworld.emc.module.item.staffabilitycheck.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.staffabilitycheck.resource.EmcStaffAbalityCheckResource;
import com.sunwayworld.emc.module.item.staffabilitycheck.service.EmcStaffAbalityCheckService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("能力验证表")
@RestController
@GikamBean
public class EmcStaffAbalityCheckResourceImpl implements EmcStaffAbalityCheckResource {

    @Autowired
    private EmcStaffAbalityCheckService emcStaffAbalityCheckService;

    @Override
    public EmcStaffAbalityCheckService getService() {
        return emcStaffAbalityCheckService;
    }

    @Log(value = "新增能力验证表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
