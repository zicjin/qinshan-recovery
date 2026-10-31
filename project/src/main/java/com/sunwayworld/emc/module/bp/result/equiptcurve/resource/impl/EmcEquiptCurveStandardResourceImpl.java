package com.sunwayworld.emc.module.bp.result.equiptcurve.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.result.equiptcurve.resource.EmcEquiptCurveStandardResource;
import com.sunwayworld.emc.module.bp.result.equiptcurve.service.EmcEquiptCurveStandardService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("仪器曲线标液配置")
@RestController
@GikamBean
public class EmcEquiptCurveStandardResourceImpl implements EmcEquiptCurveStandardResource {

    @Autowired
    private EmcEquiptCurveStandardService emcEquiptCurveStandardService;

    @Override
    public EmcEquiptCurveStandardService getService() {
        return emcEquiptCurveStandardService;
    }

    @Log(value = "新增仪器曲线标液配置", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
