package com.sunwayworld.emc.module.bp.voc.service;

import com.sunwayworld.emc.module.bp.voc.bean.PfVocWeightingBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author licm@sunwayworld.com 2021年09月24日
 */
public interface PfVocWeightingService extends GenericService<PfVocWeightingBean, Long> {

    String addVoc(RestJsonWrapperBean wrapper);
    
    void insertDeliverBottle(RestJsonWrapperBean wrapper);

}
