package com.sunwayworld.emc.module.bp.result.curve.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.result.curve.resource.EmcCurveResource;
import com.sunwayworld.emc.module.bp.result.curve.service.EmcCurveService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-12
 */

@LogModule("曲线表")
@RestController
@GikamBean
public class EmcCurveResourceImpl implements EmcCurveResource {
    @Autowired
    private EmcCurveService curveService;

    @Override
    public EmcCurveService getService() {
        return curveService;
    }

    @Override
    @Log(value = "复制", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean wrapper) {
        curveService.copy(wrapper);
    }

    @Override
    @Log(value = "曲线计算", type = LogType.UPDATE)
    @RequestMapping(value = "/{id}/action/compute", method = RequestMethod.PUT)
    public void compute(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        curveService.compute(id, wrapper);
    }

}
