package com.sunwayworld.emc.module.bp.delivery.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.delivery.resource.EmcDeliveryResource;
import com.sunwayworld.emc.module.bp.delivery.service.EmcDeliveryService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月22日
 */
@LogModule("交接单")
@RestController
@GikamBean
public class EmcDeliveryResourceImpl implements EmcDeliveryResource {

    @Autowired
    private EmcDeliveryService deliveryService;

    @Override
    public EmcDeliveryService getService() {
        return deliveryService;
    }

    @Override
    @Log(value = "交接单提交", type = LogType.UPDATE)
    @RequestMapping(value = "/action/submit", method = RequestMethod.POST)
    public void submit(RestJsonWrapperBean wrapper) {
        getService().submit(wrapper);
    }
}
