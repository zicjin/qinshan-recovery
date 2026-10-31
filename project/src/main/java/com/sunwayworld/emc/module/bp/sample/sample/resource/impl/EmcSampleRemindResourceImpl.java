package com.sunwayworld.emc.module.bp.sample.sample.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleRemindBean;
import com.sunwayworld.emc.module.bp.sample.sample.resource.EmcSampleRemindResource;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleRemindService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("采样单检查")
@RestController
@GikamBean
public class EmcSampleRemindResourceImpl implements EmcSampleRemindResource {

    @Autowired
    private EmcSampleRemindService emcSampleRemindService;

    @Override
    public EmcSampleRemindService getService() {
        return emcSampleRemindService;
    }

    @Log(value = "新增采样单检查", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @RequestMapping(value = "/remind/queries",method = RequestMethod.POST)
    public Page<EmcSampleRemindBean> selectRemindBySample(RestJsonWrapperBean wrapper) {
        return getService().selectRemindBySample(wrapper);
    }


}
