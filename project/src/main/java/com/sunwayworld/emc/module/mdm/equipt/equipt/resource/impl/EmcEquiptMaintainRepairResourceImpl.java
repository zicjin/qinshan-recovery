package com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equipt.resource.EmcEquiptMaintainRepairResource;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptMaintainRepairService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("仪器维护维修")
@RestController
@GikamBean
public class EmcEquiptMaintainRepairResourceImpl implements EmcEquiptMaintainRepairResource {

    @Autowired
    private EmcEquiptMaintainRepairService emcEquiptMaintainRepairService;

    @Override
    public EmcEquiptMaintainRepairService getService() {
        return emcEquiptMaintainRepairService;
    }

    @Log(value = "新增仪器维护维修", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
