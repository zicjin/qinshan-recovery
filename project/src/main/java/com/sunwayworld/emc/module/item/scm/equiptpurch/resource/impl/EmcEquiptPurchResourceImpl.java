package com.sunwayworld.emc.module.item.scm.equiptpurch.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.scm.equiptpurch.resource.EmcEquiptPurchResource;
import com.sunwayworld.emc.module.item.scm.equiptpurch.service.EmcEquiptPurchService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("仪器采购")
@RestController
@GikamBean
public class EmcEquiptPurchResourceImpl implements EmcEquiptPurchResource {
    @Autowired
    private EmcEquiptPurchService equiptPurchService;
    @Override
    public EmcEquiptPurchService getService() {
        return equiptPurchService;
    }


    @Log(value = "新增仪器采购", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
