package com.sunwayworld.emc.module.bp.delivery.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.delivery.bean.EmcDeliveryBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcDeliveryService extends GenericService<EmcDeliveryBean, Long> {

    void createDelivery(List<EmcSampleBean> sampleList);

    void submit(RestJsonWrapperBean wrapper);
}
