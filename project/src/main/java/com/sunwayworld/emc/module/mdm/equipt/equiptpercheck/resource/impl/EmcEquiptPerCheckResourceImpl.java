package com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.resource.EmcEquiptPerCheckResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.service.EmcEquiptPerCheckService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("期间核查表")
@RestController
@GikamBean
public class EmcEquiptPerCheckResourceImpl implements EmcEquiptPerCheckResource {

    @Autowired
    private EmcEquiptPerCheckService emcEquiptPerCheckService;

    @Override
    public EmcEquiptPerCheckService getService() {
        return emcEquiptPerCheckService;
    }

    @Log(value = "新增期间核查表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }


    @Override
    @Log(value = "期间审查审核通过，更新维护计划", type = LogType.UPDATE)
    @RequestMapping(value = "/per-check-equipt-plan", method = RequestMethod.POST)
    public void updateRecordPlanOnPass(RestJsonWrapperBean wrapperBean) {
        getService().updateEquiptPlanOnPass(wrapperBean);
    }
}
