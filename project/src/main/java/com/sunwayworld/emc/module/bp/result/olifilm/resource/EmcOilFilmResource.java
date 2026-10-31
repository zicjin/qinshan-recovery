package com.sunwayworld.emc.module.bp.result.olifilm.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.olifilm.bean.EmcOilFilmBean;
import com.sunwayworld.emc.module.bp.result.olifilm.service.EmcOilFilmService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/result/oil-films")
public interface EmcOilFilmResource extends GenericResource<EmcOilFilmService, EmcOilFilmBean, Long> {
	
	String batchAdd(RestJsonWrapperBean wrapper);

}
