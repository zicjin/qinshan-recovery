package com.sunwayworld.emc.module.bp.voc.resource;


import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.voc.bean.PfVocWeightingBean;
import com.sunwayworld.emc.module.bp.voc.service.PfVocWeightingService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author licm@sunwayworld.com 2021年09月24日
 */
@RequestMapping("/secure/emc/module/bp/voc")
public interface PfVocWeightingResource extends GenericResource<PfVocWeightingService, PfVocWeightingBean, Long> {

    String addVoc(RestJsonWrapperBean wrapper);

    void insertDeliverBottle(RestJsonWrapperBean wrapper);

}
