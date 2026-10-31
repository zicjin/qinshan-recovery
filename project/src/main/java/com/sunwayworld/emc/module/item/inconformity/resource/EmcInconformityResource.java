package com.sunwayworld.emc.module.item.inconformity.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.inconformity.bean.EmcInconformityBean;
import com.sunwayworld.emc.module.item.inconformity.service.EmcInconformityService;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-12-6
 */
@RequestMapping("/secure/emc/module/item/inconformitys")
public interface EmcInconformityResource extends GenericResource<EmcInconformityService, EmcInconformityBean, Long>,
                                                 GenericAuditableResource<EmcInconformityService, EmcInconformityBean, Long> {
}
