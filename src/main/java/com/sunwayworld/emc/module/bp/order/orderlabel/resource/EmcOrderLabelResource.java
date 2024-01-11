package com.sunwayworld.emc.module.bp.order.orderlabel.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.order.orderlabel.bean.EmcOrderLabelBean;
import com.sunwayworld.emc.module.bp.order.orderlabel.service.EmcOrderLabelService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
* @author zhengxy@sunwayworld.com 2019年10月18日
*/
@RequestMapping("/secure/emc/module/bp/order/order-labels")
public interface EmcOrderLabelResource extends GenericResource<EmcOrderLabelService, EmcOrderLabelBean, Long> {

}
