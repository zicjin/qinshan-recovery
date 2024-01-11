package com.sunwayworld.emc.module.item.scm.convenmtl.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.scm.convenmtl.bean.EmcConvenMtlBean;
import com.sunwayworld.emc.module.item.scm.convenmtl.service.EmcConvenMtlService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/scm/conven-mtls")
public interface EmcConvenMtlResource extends GenericResource<EmcConvenMtlService, EmcConvenMtlBean, Long> ,GenericAuditableResource<EmcConvenMtlService, EmcConvenMtlBean, Long>{
        
        Long insert(RestJsonWrapperBean wrapper);
        
}
