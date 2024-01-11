package com.sunwayworld.emc.module.mdm.basemdm.cert.service;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertMonitorAbilityBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

public interface EmcCertMonitorAbilityService extends GenericService<EmcCertMonitorAbilityBean, Long> {
    void insert(Long certId, RestJsonWrapperBean wrapper);
}
