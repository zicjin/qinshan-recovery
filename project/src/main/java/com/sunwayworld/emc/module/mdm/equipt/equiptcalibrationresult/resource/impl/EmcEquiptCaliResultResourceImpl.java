package com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.resource.EmcEquiptCaliResultResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.service.EmcEquiptCaliResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.bean.EmcEquiptCaliResultNormBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("校准结果确认记录")
@RestController
@GikamBean
public class EmcEquiptCaliResultResourceImpl implements EmcEquiptCaliResultResource {

    @Autowired
    private EmcEquiptCaliResultService emcEquiptCaliResultService;

    @Override
    public EmcEquiptCaliResultService getService() {
        return emcEquiptCaliResultService;
    }

    @Log(value = "新增校准结果确认记录", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "插入测试方法", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/test-methods", method = RequestMethod.POST)
    public Long insertTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertTestMethod(id, wrapper);
    }

    @Override
    @Log(value = "查询测试方法", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/test-methods/queries", method = RequestMethod.POST)
    public Page<EmcEquiptCaliResultNormBean> selectEquiptTestMethodRawPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectEquiptTestMethodRawPagination(id, wrapper);
    }

    @Override
    @Log(value = "审核通过，更新维护计划", type = LogType.UPDATE)
    @RequestMapping(value = "/equipt-plan", method = RequestMethod.POST)
    public void updateRecordPlanOnPass(RestJsonWrapperBean wrapperBean) {
        getService().updateEquiptPlanOnPass(wrapperBean);
    }
}
