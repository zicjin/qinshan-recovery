package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.resource.EmcEquipAppearanceResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipAppearanceService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("仪器外观检查表")
@RestController
@GikamBean
public class EmcEquipAppearanceResourceImpl implements EmcEquipAppearanceResource {

    @Autowired
    private EmcEquipAppearanceService emcEquipAppearanceService;

    @Override
    public EmcEquipAppearanceService getService() {
        return emcEquipAppearanceService;
    }

    @Log(value = "新增仪器外观检查表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
