package com.sunwayworld.emc.module.item.qualitycontrol.service;

import com.sunwayworld.emc.module.item.qualitycontrol.bean.EmcQualityControlBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magt@sunwayworld.com
 * @date 2019-12-04
 */
public interface EmcQualityControlService extends GenericService<EmcQualityControlBean, Long>, GenericAuditableService<EmcQualityControlBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);
}
