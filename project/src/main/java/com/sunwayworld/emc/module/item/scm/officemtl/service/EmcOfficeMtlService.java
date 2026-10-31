package com.sunwayworld.emc.module.item.scm.officemtl.service;

import com.sunwayworld.emc.module.item.scm.officemtl.bean.EmcOfficeMtlBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcOfficeMtlService extends GenericService<EmcOfficeMtlBean, Long> ,GenericAuditableService<EmcOfficeMtlBean, Long>{
    Long insert(RestJsonWrapperBean wrapper);
}
