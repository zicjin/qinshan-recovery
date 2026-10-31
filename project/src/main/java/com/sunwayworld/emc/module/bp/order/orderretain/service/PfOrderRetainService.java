package com.sunwayworld.emc.module.bp.order.orderretain.service;


import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author licm@sunwayworld.com 2021年10月15日
*/

public interface PfOrderRetainService extends EmcOrderRetainService{
    void confirmRetainOrder(RestJsonWrapperBean wrapper);
}
