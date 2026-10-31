package com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.bean.EmcEquiptAcqParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.service.EmcEquiptAcqParamService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-acq-params")
public interface EmcEquiptAcqParamResource
        extends GenericResource<EmcEquiptAcqParamService, EmcEquiptAcqParamBean, Long>, GenericChoosableResource<EmcEquiptAcqParamService, EmcEquiptAcqParamBean, Long> {

}
