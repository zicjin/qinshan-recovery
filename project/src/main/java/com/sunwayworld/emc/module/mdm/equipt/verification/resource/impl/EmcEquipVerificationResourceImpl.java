package com.sunwayworld.emc.module.mdm.equipt.verification.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.verification.resource.EmcEquipVerificationResource;
import com.sunwayworld.emc.module.mdm.equipt.verification.service.EmcEquipVerificationService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("检定结果确认记录表")
@RestController
@GikamBean
public class EmcEquipVerificationResourceImpl implements EmcEquipVerificationResource {

    @Autowired
    private EmcEquipVerificationService emcEquipVerificationService;

    @Override
    public EmcEquipVerificationService getService() {
        return emcEquipVerificationService;
    }

    @Log(value = "新增检定结果确认记录表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "审核通过，更新维护计划", type = LogType.UPDATE)
    @RequestMapping(value = "/equipt-plan", method = RequestMethod.POST)
    public void updateRecordPlanOnPass(RestJsonWrapperBean wrapperBean) {
        getService().updateEquiptPlanOnPass(wrapperBean);
    }
}
