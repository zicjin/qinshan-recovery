package com.sunwayworld.emc.module.item.qualitysupervision.service;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionContBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
public interface EmcQualitySupervisionContService extends GenericService<EmcQualitySupervisionContBean, Long> {
    Long insert(Long planId, RestJsonWrapperBean wrapper);
}
