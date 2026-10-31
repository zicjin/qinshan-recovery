package com.sunwayworld.emc.module.mdm.bizmdm.client.service;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

public interface EmcMonitorPointService extends GenericService<EmcMonitorPointBean, Long>, GenericSelectableService<EmcMonitorPointBean, Long>, GenericChoosableService<EmcMonitorPointBean, Long> {
    void insert(Long id, RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean wrapper);
}
