package com.sunwayworld.emc.module.bp.result.curve.service;

import com.sunwayworld.emc.module.bp.result.curve.bean.EmcEquiptCurveConfigBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquiptCurveConfigService extends GenericService<EmcEquiptCurveConfigBean, Long> {

    Long insert(Long equiptCurveId, RestJsonWrapperBean wrapper);

    void copyEquiptCurveConfig(RestJsonWrapperBean wrapper);
}
