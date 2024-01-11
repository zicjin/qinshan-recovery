package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.EmcEquiptCalibrationService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
@RequestMapping("/secure/emc/module/mdm/equipt/equipt-calibrations")
public interface EmcEquiptCalibrationResource extends GenericResource<EmcEquiptCalibrationService, EmcEquiptCalibrationBean, Long> {

    Page<EmcEquiptCalibrationLineBean> selectCalibrationLinePagination(Long id, RestJsonWrapperBean wrapper);

    void insertCalibrationLine(RestJsonWrapperBean wrapper);

    void deleteCalibrationLine(RestJsonWrapperBean wrapper);

    void samplingBatchInput(RestJsonWrapperBean wrapper);
}
