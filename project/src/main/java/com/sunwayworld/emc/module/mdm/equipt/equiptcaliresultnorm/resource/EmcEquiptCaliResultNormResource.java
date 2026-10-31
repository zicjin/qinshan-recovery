package com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.bean.EmcEquiptCaliResultNormBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.service.EmcEquiptCaliResultNormService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-cali-result-norms")
public interface EmcEquiptCaliResultNormResource extends GenericResource<EmcEquiptCaliResultNormService, EmcEquiptCaliResultNormBean, Long> {
}
