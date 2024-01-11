package com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.bean.EmcEquiptPerCheckBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquiptPerCheckService extends GenericService<EmcEquiptPerCheckBean, Long>, GenericAuditableService<EmcEquiptPerCheckBean, Long> {

    void updateEquiptPlanOnPass(RestJsonWrapperBean wrapperBean);
}
