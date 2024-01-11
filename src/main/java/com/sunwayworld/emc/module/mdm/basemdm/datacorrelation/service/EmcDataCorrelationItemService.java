package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service;

import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationItemBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */
@GikamBean
public interface EmcDataCorrelationItemService extends GenericService<EmcDataCorrelationItemBean, Long> {
    void insertMul(RestJsonWrapperBean jsonWrapper);
}
