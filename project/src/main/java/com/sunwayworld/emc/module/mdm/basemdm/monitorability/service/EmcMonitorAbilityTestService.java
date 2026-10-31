package com.sunwayworld.emc.module.mdm.basemdm.monitorability.service;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityTestBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

public interface EmcMonitorAbilityTestService extends GenericService<EmcMonitorAbilityTestBean, Long> {
    Long insert(Long moniAbilId, RestJsonWrapperBean wrapper);
}
