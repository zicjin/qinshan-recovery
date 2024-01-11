package com.sunwayworld.emc.module.bp.result.equiptcurve.service;

import com.sunwayworld.emc.module.bp.result.equiptcurve.bean.EmcEquiptCurveBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEquiptCurveService extends GenericService<EmcEquiptCurveBean, Long>, GenericChoosableService<EmcEquiptCurveBean, Long> {

    void copy(RestJsonWrapperBean wrapper);

    void batchUnit(String ids, RestJsonWrapperBean wrapper);

    Long insertStandard(Long id, RestJsonWrapperBean wrapper);

    Long insertByEquiptId(Long equiptId, RestJsonWrapperBean wrapper);
}
