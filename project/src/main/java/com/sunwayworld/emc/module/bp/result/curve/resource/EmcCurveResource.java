package com.sunwayworld.emc.module.bp.result.curve.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.curve.bean.EmcCurveBean;
import com.sunwayworld.emc.module.bp.result.curve.service.EmcCurveService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-12
 */
@RequestMapping("/secure/emc/module/bp/result/curves")
public interface EmcCurveResource extends GenericResource<EmcCurveService, EmcCurveBean, Long> {
    void copy(RestJsonWrapperBean wrapper);

    void compute(Long id, RestJsonWrapperBean wrapper);
}
