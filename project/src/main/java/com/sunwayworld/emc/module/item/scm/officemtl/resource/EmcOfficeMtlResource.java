package com.sunwayworld.emc.module.item.scm.officemtl.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.scm.officemtl.bean.EmcOfficeMtlBean;
import com.sunwayworld.emc.module.item.scm.officemtl.service.EmcOfficeMtlService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/scm/office-mtls")
public interface EmcOfficeMtlResource extends GenericResource<EmcOfficeMtlService, EmcOfficeMtlBean, Long> ,GenericAuditableResource<EmcOfficeMtlService, EmcOfficeMtlBean, Long>{
        
        Long insert(RestJsonWrapperBean wrapper);
        
}
