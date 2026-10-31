package com.sunwayworld.emc.module.bp.bp.service;

import com.sunwayworld.emc.module.bp.bp.bean.EmcResultLogBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-21
 */

public interface EmcResultLogService extends GenericService<EmcResultLogBean, Long> {
    Long insert(Long id, RestJsonWrapperBean wrapper);
}
