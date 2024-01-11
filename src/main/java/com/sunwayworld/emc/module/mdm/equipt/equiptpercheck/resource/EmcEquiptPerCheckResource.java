package com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.bean.EmcEquiptPerCheckBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.service.EmcEquiptPerCheckService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-per-checks")
public interface EmcEquiptPerCheckResource extends GenericResource<EmcEquiptPerCheckService, EmcEquiptPerCheckBean, Long>, GenericAuditableResource<EmcEquiptPerCheckService, EmcEquiptPerCheckBean, Long> {
    void updateRecordPlanOnPass(RestJsonWrapperBean wrapperBean);

}
