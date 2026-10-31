package com.sunwayworld.emc.module.item.quotation.quotationinformation.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.quotation.quotationinformation.resource.EmcQuotationinformationResource;
import com.sunwayworld.emc.module.item.quotation.quotationinformation.service.EmcQuotationinformationService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("报价单信息表")
@RestController
@GikamBean
public class EmcQuotationinformationResourceImpl implements EmcQuotationinformationResource {

    @Autowired
    private EmcQuotationinformationService emcQuotationinformationService;

    @Override
    public EmcQuotationinformationService getService() {
        return emcQuotationinformationService;
    }

    @Log(value = "新增报价单信息表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
