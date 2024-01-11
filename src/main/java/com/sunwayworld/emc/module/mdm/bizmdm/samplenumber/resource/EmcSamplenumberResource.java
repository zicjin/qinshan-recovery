package com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.bean.EmcSamplenumberBean;
import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.service.EmcSamplenumberService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/bizmdm/samplenumbers")
public interface EmcSamplenumberResource extends GenericResource<EmcSamplenumberService, EmcSamplenumberBean, Long> {
}
