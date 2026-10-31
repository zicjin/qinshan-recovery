package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestmethodBeforehandleBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcTestmethodBeforehandleService extends GenericService<EmcTestmethodBeforehandleBean, Long>, GenericChoosableService<EmcTestmethodBeforehandleBean, Long> {
    Long insert(Long testMethodId, RestJsonWrapperBean wrapper);
}
