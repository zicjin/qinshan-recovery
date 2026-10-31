package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service.EmcEquiptAcqResultService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-acq-results")
public interface EmcEquiptAcqResultResource
        extends GenericResource<EmcEquiptAcqResultService, EmcEquiptAcqResultBean, Long>, GenericChoosableResource<EmcEquiptAcqResultService, EmcEquiptAcqResultBean, Long> {

}
