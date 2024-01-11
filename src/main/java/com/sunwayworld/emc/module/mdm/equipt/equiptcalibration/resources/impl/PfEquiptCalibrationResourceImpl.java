package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.resources.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.resource.impl.EmcEquiptCalibrationResourceImpl;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.resources.PfEquiptCalibrationResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.PfEquiptCalibrationService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;


@GikamBean
@RestController
@LogModule(value = "仪器校准")
public class PfEquiptCalibrationResourceImpl extends EmcEquiptCalibrationResourceImpl implements PfEquiptCalibrationResource {

    @Autowired
    private PfEquiptCalibrationService calibrationService;

    @Override
    @Log(value = "新增", type = LogType.INSERT)
    @RequestMapping(value = "/action/insert/{sampleId}", method = RequestMethod.POST)
    public Long insert(@PathVariable Long sampleId,RestJsonWrapperBean wrapper) {
       return calibrationService.insert(sampleId,wrapper);
    }
}