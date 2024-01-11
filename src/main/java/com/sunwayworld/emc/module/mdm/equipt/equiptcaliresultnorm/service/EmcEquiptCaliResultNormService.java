package com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.bean.EmcEquiptCaliResultNormBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquiptCaliResultNormService extends GenericService<EmcEquiptCaliResultNormBean, Long> {

    Long insert(Long id, RestJsonWrapperBean wrapper);
}
