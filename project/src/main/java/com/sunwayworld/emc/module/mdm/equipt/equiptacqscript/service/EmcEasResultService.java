package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEasResultService extends GenericService<EmcEasResultBean, Long>, GenericChoosableService<EmcEasResultBean, Long> {

    Long insert(Long scriptId);

}
