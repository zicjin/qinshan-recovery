package com.sunwayworld.emc.module.bp.result.olifilm.service;

import com.sunwayworld.emc.module.bp.result.olifilm.bean.EmcOilFilmBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcOilFilmService extends GenericService<EmcOilFilmBean, Long> {

	String batchAdd(RestJsonWrapperBean wrapper);

}
