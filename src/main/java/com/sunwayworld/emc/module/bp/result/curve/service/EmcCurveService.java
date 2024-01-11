package com.sunwayworld.emc.module.bp.result.curve.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.result.curve.bean.EmcCurveBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-12
 */

public interface EmcCurveService extends GenericService<EmcCurveBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean wrapper);

    void compute(Long id, RestJsonWrapperBean wrapper);

    List<EmcOrderTaskResultBean> calculateSlopeIntercept(List<EmcOrderTaskResultBean> curveOrderTaskResultList, List<EmcItemBean> curveItemList);
}
