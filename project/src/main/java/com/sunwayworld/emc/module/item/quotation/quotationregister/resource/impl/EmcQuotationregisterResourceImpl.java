package com.sunwayworld.emc.module.item.quotation.quotationregister.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.quotation.quotationregister.resource.EmcQuotationregisterResource;
import com.sunwayworld.emc.module.item.quotation.quotationregister.service.EmcQuotationregisterService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("报价单登记表")
@RestController
@GikamBean
public class EmcQuotationregisterResourceImpl implements EmcQuotationregisterResource {

    @Autowired
    private EmcQuotationregisterService emcQuotationregisterService;

    @Override
    public EmcQuotationregisterService getService() {
        return emcQuotationregisterService;
    }

    @Log(value = "新增报价单登记表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "添加折扣", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/addDiscount", method = RequestMethod.POST)
    public void addDiscount(RestJsonWrapperBean wrapper) {
        getService().addDiscount(wrapper);
    }

    @Log(value = "计算", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/calculation", method = RequestMethod.POST)
    public void calculation(RestJsonWrapperBean wrapper) {
        getService().calculation(wrapper);
    }

    @Log(value = "添加合同", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/addContract", method = RequestMethod.POST)
    public void addContract(RestJsonWrapperBean wrapper) {
        getService().addContract(wrapper);
    }
}
