package com.sunwayworld.emc.module.item.scm.standardmtl.service;

import com.sunwayworld.emc.module.item.scm.standardmtl.bean.EmcStandardMtlBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcStandardMtlService extends GenericService<EmcStandardMtlBean, Long>,GenericAuditableService<EmcStandardMtlBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);
}
