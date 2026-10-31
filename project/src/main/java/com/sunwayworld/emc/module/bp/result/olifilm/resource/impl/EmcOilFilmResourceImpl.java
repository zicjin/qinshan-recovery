package com.sunwayworld.emc.module.bp.result.olifilm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.result.olifilm.resource.EmcOilFilmResource;
import com.sunwayworld.emc.module.bp.result.olifilm.service.EmcOilFilmService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("油膜滤筒信息表")
@RestController
@GikamBean
public class EmcOilFilmResourceImpl implements EmcOilFilmResource {

    @Autowired
    private EmcOilFilmService emcOilFilmService;

    @Override
    public EmcOilFilmService getService() {
        return emcOilFilmService;
    }
    
    @Log(value = "新增油膜滤筒信息表", type = LogType.INSERT)
	@Override
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
	public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
	}
    
    @Log(value = "根据瓶数量增加", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/batch-add", method = RequestMethod.POST)
    public String batchAdd(RestJsonWrapperBean wrapper) {
        return emcOilFilmService.batchAdd(wrapper);
    }

}
