package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
public interface EmcEquiptCalibrationService extends GenericService<EmcEquiptCalibrationBean, Long> {

    Page<EmcEquiptCalibrationLineBean> selectCalibrationLinePagination(Long id, RestJsonWrapperBean wrapper);

    void insertCalibrationLine(RestJsonWrapperBean wrapper);

    void deleteCalibrationLine(RestJsonWrapperBean wrapper);

    void samplingBatchInput(RestJsonWrapperBean wrapper);
}
