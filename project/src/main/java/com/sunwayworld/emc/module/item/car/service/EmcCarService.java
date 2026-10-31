package com.sunwayworld.emc.module.item.car.service;

import com.sunwayworld.emc.module.item.car.bean.EmcCarBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

public interface EmcCarService extends GenericService<EmcCarBean, Long>, GenericAuditableService<EmcCarBean, Long>, GenericChoosableService<EmcCarBean, Long> {
    @Override
    Long insert(RestJsonWrapperBean wrapper);
}
