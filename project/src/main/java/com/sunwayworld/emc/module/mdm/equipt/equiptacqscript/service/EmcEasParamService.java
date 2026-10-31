package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasParamBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEasParamService extends GenericService<EmcEasParamBean, Long>, GenericChoosableService<EmcEasParamBean, Long> {

    void insert(Long scriptId, RestJsonWrapperBean wrapper);

}
