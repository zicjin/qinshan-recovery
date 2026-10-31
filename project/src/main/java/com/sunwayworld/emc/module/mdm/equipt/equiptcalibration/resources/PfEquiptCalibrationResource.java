package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.resources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.resource.EmcEquiptCalibrationResource;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;


@RequestMapping("/secure/emc/module/mdm/equipt/equipt-calibrations")
public interface PfEquiptCalibrationResource extends EmcEquiptCalibrationResource {

    Long insert(@PathVariable Long sampleId,RestJsonWrapperBean wrapper);
}
