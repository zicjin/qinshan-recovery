package com.sunwayworld.emc.module.item.quotation.detectionfactor.service;

import com.sunwayworld.emc.module.item.quotation.detectionfactor.bean.EmcDetectionfactorBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface EmcDetectionfactorService extends GenericService<EmcDetectionfactorBean, Long>, GenericChoosableService<EmcDetectionfactorBean, Long> {

    Long insertByItemGroup(RestJsonWrapperBean wrapper);
}
