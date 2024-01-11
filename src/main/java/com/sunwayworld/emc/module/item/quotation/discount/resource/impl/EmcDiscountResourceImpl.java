package com.sunwayworld.emc.module.item.quotation.discount.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.quotation.discount.resource.EmcDiscountResource;
import com.sunwayworld.emc.module.item.quotation.discount.service.EmcDiscountService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("折扣管理表")
@RestController
@GikamBean
public class EmcDiscountResourceImpl implements EmcDiscountResource {

    @Autowired
    private EmcDiscountService emcDiscountService;

    @Override
    public EmcDiscountService getService() {
        return emcDiscountService;
    }

    @Log(value = "新增折扣管理表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
