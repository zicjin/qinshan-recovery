package com.sunwayworld.emc.module.item.staffability.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.staffability.resource.EmcStaffAbilityResource;
import com.sunwayworld.emc.module.item.staffability.service.EmcStaffAbilityService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("人员能力表")
@RestController
@GikamBean
public class EmcStaffAbilityResourceImpl implements EmcStaffAbilityResource {

    @Autowired
    private EmcStaffAbilityService emcStaffAbilityService;

    @Override
    public EmcStaffAbilityService getService() {
        return emcStaffAbilityService;
    }

    @Log(value = "新增人员能力表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
