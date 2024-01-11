package com.sunwayworld.emc.module.mdm.basemdm.monitorability.service;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

public interface EmcMonitorAbilityService extends GenericService<EmcMonitorAbilityBean, Long>, GenericChoosableService<EmcMonitorAbilityBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);
}
