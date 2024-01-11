package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-13
 */

public interface EmcEquiptTestItemService extends GenericService<EmcEquiptTestItemBean, Long> {
    Long insert(Long id, RestJsonWrapperBean wrapper);
}
