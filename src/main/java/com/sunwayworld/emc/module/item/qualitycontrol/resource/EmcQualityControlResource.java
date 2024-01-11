package com.sunwayworld.emc.module.item.qualitycontrol.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.qualitycontrol.bean.EmcQualityControlBean;
import com.sunwayworld.emc.module.item.qualitycontrol.service.EmcQualityControlService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author magt@sunwayworld.com
 * @date 2019-12-04
 */
@RequestMapping("/secure/emc/module/item/quality-controls")
public interface EmcQualityControlResource extends GenericResource<EmcQualityControlService, EmcQualityControlBean, Long>, GenericAuditableResource<EmcQualityControlService, EmcQualityControlBean, Long>{

    Long insert(RestJsonWrapperBean wrapper);
}
