package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
public interface EmcEquiptCalibrationLineService extends GenericService<EmcEquiptCalibrationLineBean, Long> {

    void insert(List<EmcEquiptCalibrationBean> calibrationList);
}
