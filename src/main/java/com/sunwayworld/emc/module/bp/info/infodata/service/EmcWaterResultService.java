package com.sunwayworld.emc.module.bp.info.infodata.service;

import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcWaterResultBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcWaterResultService extends GenericService<EmcWaterResultBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);
    
    void calculate(RestJsonWrapperBean wrapper);
}
