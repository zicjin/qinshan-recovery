package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasPreAnalyBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEasPreAnalyService extends GenericService<EmcEasPreAnalyBean, Long>, GenericChoosableService<EmcEasPreAnalyBean, Long> {

    Long insert(Long scriptId);

}
