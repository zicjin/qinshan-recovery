package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service;

import org.springframework.web.bind.annotation.PathVariable;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;


public interface PfEquiptCalibrationService extends EmcEquiptCalibrationService {

    Long insert(@PathVariable Long sampleId,RestJsonWrapperBean wrapper);
}
