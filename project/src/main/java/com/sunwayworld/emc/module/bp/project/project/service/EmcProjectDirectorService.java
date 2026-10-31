package com.sunwayworld.emc.module.bp.project.project.service;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcProjectDirectorService extends GenericService<EmcProjectDirectorBean, Long> {

    Long insert(RestJsonWrapperBean wrapper);

}
