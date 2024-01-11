package com.sunwayworld.emc.module.item.scm.standardmtl.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.scm.standardmtl.bean.EmcStandardMtlBean;
import com.sunwayworld.emc.module.item.scm.standardmtl.service.EmcStandardMtlService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/scm/standard-mtls")
public interface EmcStandardMtlResource extends GenericResource<EmcStandardMtlService, EmcStandardMtlBean, Long>,GenericAuditableResource<EmcStandardMtlService, EmcStandardMtlBean, Long> {
        
        Long insert(RestJsonWrapperBean wrapper);
        
}
