package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.resource.EmcEquiptCalibrationResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.EmcEquiptCalibrationService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
@GikamBean
@RestController
@LogModule(value = "仪器校准")
public class EmcEquiptCalibrationResourceImpl implements EmcEquiptCalibrationResource {

    @Autowired
    private EmcEquiptCalibrationService emcEquiptCalibrationService;

    @Override
    public EmcEquiptCalibrationService getService() {
        return emcEquiptCalibrationService;
    }

    @Override
    @Log(value = "根据项目编号查询项目负责人", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/lines/queries", method = RequestMethod.POST)
    public Page<EmcEquiptCalibrationLineBean> selectCalibrationLinePagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectCalibrationLinePagination(id, wrapper);
    }

    @Override
    @Log(value = "新增校准记录", type = LogType.INSERT)
    @RequestMapping(value = "/lines", method = RequestMethod.POST)
    public void insertCalibrationLine(RestJsonWrapperBean wrapper) {
        getService().insertCalibrationLine(wrapper);
    }

    @Override
    @Log(value = "删除校准记录", type = LogType.DELETE)
    @RequestMapping(value = "/lines", method = RequestMethod.DELETE)
    public void deleteCalibrationLine(RestJsonWrapperBean wrapper) {
        getService().deleteCalibrationLine(wrapper);
    }

    @Override
    @Log(value = "大气采样类批录入", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sampling-batch-input", method = RequestMethod.POST)
    public void samplingBatchInput(RestJsonWrapperBean wrapper) {
        getService().samplingBatchInput(wrapper);
    }
}
