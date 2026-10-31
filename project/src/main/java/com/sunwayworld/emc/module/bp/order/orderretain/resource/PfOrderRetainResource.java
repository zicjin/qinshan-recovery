package com.sunwayworld.emc.module.bp.order.orderretain.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author licm@sunwayworld.com 2021/10/15
 */
@RequestMapping("/secure/emc/module/bp/order/order-retains")
public interface PfOrderRetainResource extends EmcOrderRetainResource{
    void confirmRetainOrder(RestJsonWrapperBean wrapper);
}
