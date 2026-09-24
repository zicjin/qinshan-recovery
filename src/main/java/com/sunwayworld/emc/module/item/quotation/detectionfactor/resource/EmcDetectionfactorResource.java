package com.sunwayworld.emc.module.item.quotation.detectionfactor.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.quotation.detectionfactor.bean.EmcDetectionfactorBean;
import com.sunwayworld.emc.module.item.quotation.detectionfactor.service.EmcDetectionfactorService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@RequestMapping("/secure/emc/module/item/quotation/detectionfactors")
public interface EmcDetectionfactorResource extends GenericResource<EmcDetectionfactorService, EmcDetectionfactorBean, Long>, GenericChoosableResource<EmcDetectionfactorService, EmcDetectionfactorBean, Long> {

    Long insertByItemGroup(RestJsonWrapperBean wrapper);
}
