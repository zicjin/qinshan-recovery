package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magt@sunwayworld.com
 * @date 2019-09-20
 */

public interface EmcEquiptTestMethodService extends GenericService<EmcEquiptTestMethodBean, Long> {
    Long insert(Long id, RestJsonWrapperBean wrapper);

    void testMethodCopyTo(RestJsonWrapperBean wrapper);
}
