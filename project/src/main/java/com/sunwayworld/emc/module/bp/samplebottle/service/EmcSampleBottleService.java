package com.sunwayworld.emc.module.bp.samplebottle.service;

import com.sunwayworld.emc.module.bp.samplebottle.bean.EmcSampleBottleBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcSampleBottleService extends GenericService<EmcSampleBottleBean, Long> {
    String addSampleBottle(RestJsonWrapperBean wrapper);


}
