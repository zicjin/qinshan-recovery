package com.sunwayworld.emc.module.bp.order.orderreceive.resource.impl;

import com.sunwayworld.emc.module.bp.order.orderreceive.resource.PfOrderReceiveResource;
import com.sunwayworld.emc.module.bp.order.orderreceive.service.PfOrderReceiveService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class PfOrderReceiveResourceImpl implements PfOrderReceiveResource {

    @Autowired
    private PfOrderReceiveService orderReceiveService;

    public PfOrderReceiveService getService() {
        return orderReceiveService;
    }

    @Override
    public Long match(RestJsonWrapperBean wrapper) {
        return getService().match(wrapper);
    }

    @Override
    public Long receive(RestJsonWrapperBean wrapper) {
        return getService().receive(wrapper);
    }
}
