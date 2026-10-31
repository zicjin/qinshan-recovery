package com.sunwayworld.emc.module.item.qualitysupervision.service;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionPlanBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
public interface EmcQualitySupervisionPlanService extends GenericService<EmcQualitySupervisionPlanBean, Long> {
    Long insert(Long supervisionId, RestJsonWrapperBean wrapper);
}
