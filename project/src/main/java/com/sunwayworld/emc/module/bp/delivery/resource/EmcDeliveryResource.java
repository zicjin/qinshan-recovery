package com.sunwayworld.emc.module.bp.delivery.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.delivery.bean.EmcDeliveryBean;
import com.sunwayworld.emc.module.bp.delivery.service.EmcDeliveryService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhengxy@sunwayworld.com 2023年08月22日
 */
@RequestMapping("/secure/emc/module/bp/deliveries")
public interface EmcDeliveryResource extends GenericResource<EmcDeliveryService, EmcDeliveryBean, Long> {

    void submit(RestJsonWrapperBean wrapper);
}
