package com.sunwayworld.emc.module.item.car.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.car.bean.EmcCarBean;
import com.sunwayworld.emc.module.item.car.service.EmcCarService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */
@RequestMapping("/secure/emc/module/item/emc-cars")
public interface EmcCarResource
        extends GenericResource<EmcCarService, EmcCarBean, Long>, GenericAuditableResource<EmcCarService, EmcCarBean, Long>, GenericChoosableResource<EmcCarService, EmcCarBean, Long> {

    @Override
    Long insert(RestJsonWrapperBean wrapper);

}
