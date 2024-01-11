package com.sunwayworld.emc.module.mdm.bizmdm.client.service;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

public interface EmcMonitorPointSchemeService extends GenericService<EmcMonitorPointSchemeBean, Long>, GenericChoosableService<EmcMonitorPointSchemeBean, Long> {
    void insert(Long monitorPointId, RestJsonWrapperBean wrapper);

    void copyMonitorPointScheme(Long monitorPointId, RestJsonWrapperBean wrapper);
}
