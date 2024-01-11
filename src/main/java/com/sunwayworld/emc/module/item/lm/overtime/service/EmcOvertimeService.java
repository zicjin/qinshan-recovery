package com.sunwayworld.emc.module.item.lm.overtime.service;

import com.sunwayworld.emc.module.item.lm.overtime.bean.EmcOvertimeBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-14
 */

public interface EmcOvertimeService extends GenericService<EmcOvertimeBean, Long>, GenericAuditableService<EmcOvertimeBean, Long> {
    @Override
    Long insert(RestJsonWrapperBean wrapper);
}
