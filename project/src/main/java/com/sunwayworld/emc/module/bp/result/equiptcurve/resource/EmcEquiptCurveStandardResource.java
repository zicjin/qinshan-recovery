package com.sunwayworld.emc.module.bp.result.equiptcurve.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.equiptcurve.bean.EmcEquiptCurveStandardBean;
import com.sunwayworld.emc.module.bp.result.equiptcurve.service.EmcEquiptCurveStandardService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/result/equipt-curve-standards")
public interface EmcEquiptCurveStandardResource extends GenericResource<EmcEquiptCurveStandardService, EmcEquiptCurveStandardBean, Long> {
}
