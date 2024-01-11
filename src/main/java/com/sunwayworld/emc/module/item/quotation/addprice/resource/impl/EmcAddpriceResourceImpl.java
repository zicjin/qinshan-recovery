package com.sunwayworld.emc.module.item.quotation.addprice.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.quotation.addprice.resource.EmcAddpriceResource;
import com.sunwayworld.emc.module.item.quotation.addprice.service.EmcAddpriceService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("附加费用表")
@RestController
@GikamBean
public class EmcAddpriceResourceImpl implements EmcAddpriceResource {

    @Autowired
    private EmcAddpriceService emcAddpriceService;

    @Override
    public EmcAddpriceService getService() {
        return emcAddpriceService;
    }

    @Log(value = "新增附加费用表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
