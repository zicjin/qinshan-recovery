package com.sunwayworld.emc.module.bp.project.projectlog.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.project.projectlog.resource.EmcProjectEditLogResource;
import com.sunwayworld.emc.module.bp.project.projectlog.service.EmcProjectEditLogService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("客服方案修改记录表")
@RestController
@GikamBean
public class EmcProjectEditLogResourceImpl implements EmcProjectEditLogResource {

    @Autowired
    private EmcProjectEditLogService emcProjectEditLogService;

    @Override
    public EmcProjectEditLogService getService() {
        return emcProjectEditLogService;
    }

    @Log(value = "新增客服方案修改记录表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
