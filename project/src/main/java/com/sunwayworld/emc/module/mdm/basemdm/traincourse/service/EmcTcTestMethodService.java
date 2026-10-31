package com.sunwayworld.emc.module.mdm.basemdm.traincourse.service;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTcTestMethodBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-28
 */

public interface EmcTcTestMethodService extends GenericService<EmcTcTestMethodBean, Long> {
    Long insert(Long id, RestJsonWrapperBean wrapper);
}
