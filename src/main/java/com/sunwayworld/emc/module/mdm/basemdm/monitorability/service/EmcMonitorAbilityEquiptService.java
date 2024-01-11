package com.sunwayworld.emc.module.mdm.basemdm.monitorability.service;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityEquiptBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

public interface EmcMonitorAbilityEquiptService extends GenericService<EmcMonitorAbilityEquiptBean, Long> {
    Long insert(Long moniAbilId, RestJsonWrapperBean wrapper);
    
    void syncEquipt(RestJsonWrapperBean wrapper);
}
