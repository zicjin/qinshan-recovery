package com.sunwayworld.emc.module.mdm.basemdm.envrecord.service;

import com.sunwayworld.emc.module.mdm.basemdm.envrecord.bean.EmcEnvRecordBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEnvRecordService extends GenericService<EmcEnvRecordBean, String>,
                                             GenericChoosableService<EmcEnvRecordBean, String> {

    String insert(RestJsonWrapperBean wrapper);

}
