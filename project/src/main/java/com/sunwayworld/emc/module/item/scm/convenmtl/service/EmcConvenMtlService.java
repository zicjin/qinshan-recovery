package com.sunwayworld.emc.module.item.scm.convenmtl.service;

import com.sunwayworld.emc.module.item.scm.convenmtl.bean.EmcConvenMtlBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcConvenMtlService extends GenericService<EmcConvenMtlBean, Long>,GenericAuditableService<EmcConvenMtlBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);
}
