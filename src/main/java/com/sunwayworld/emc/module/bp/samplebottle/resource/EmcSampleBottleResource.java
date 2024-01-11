package com.sunwayworld.emc.module.bp.samplebottle.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.samplebottle.bean.EmcSampleBottleBean;
import com.sunwayworld.emc.module.bp.samplebottle.service.EmcSampleBottleService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/sample-bottles")
public interface EmcSampleBottleResource extends GenericResource<EmcSampleBottleService, EmcSampleBottleBean, Long> {
    String addSampleBottle(RestJsonWrapperBean wrapper);

}
