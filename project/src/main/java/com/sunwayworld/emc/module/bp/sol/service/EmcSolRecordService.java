package com.sunwayworld.emc.module.bp.sol.service;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcSolRecordService extends GenericService<EmcSolRecordBean, Long> {

    void calcResult(RestJsonWrapperBean wrapper);
}
