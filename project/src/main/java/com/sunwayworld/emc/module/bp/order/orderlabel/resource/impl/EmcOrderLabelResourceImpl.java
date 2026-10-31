package com.sunwayworld.emc.module.bp.order.orderlabel.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.order.orderlabel.resource.EmcOrderLabelResource;
import com.sunwayworld.emc.module.bp.order.orderlabel.service.EmcOrderLabelService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2019年10月18日
*/
@GikamBean
@RestController
@LogModule(value = "样品标签")
public class EmcOrderLabelResourceImpl implements EmcOrderLabelResource {

    @Autowired
    private EmcOrderLabelService orderLabelService;

    @Override
    public EmcOrderLabelService getService() {
        return orderLabelService;
    }

}
