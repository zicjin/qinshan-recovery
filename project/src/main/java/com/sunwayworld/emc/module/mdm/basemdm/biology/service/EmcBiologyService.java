package com.sunwayworld.emc.module.mdm.basemdm.biology.service;

import com.sunwayworld.emc.module.mdm.basemdm.biology.bean.EmcBiologyBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.tree.service.GenericTreeService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-28
 */

public interface EmcBiologyService extends GenericService<EmcBiologyBean, Long>,GenericTreeService<EmcBiologyBean, Long> {
    @Override
    Long insert(RestJsonWrapperBean wrapper);
}
