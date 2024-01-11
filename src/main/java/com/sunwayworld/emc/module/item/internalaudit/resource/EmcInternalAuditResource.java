package com.sunwayworld.emc.module.item.internalaudit.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.internalaudit.bean.EmcInternalAuditBean;
import com.sunwayworld.emc.module.item.internalaudit.service.EmcInternalAuditService;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */
@RequestMapping("/secure/emc/module/item/internal-audits")
public interface EmcInternalAuditResource extends GenericResource<EmcInternalAuditService, EmcInternalAuditBean, Long>,
                                                  GenericAuditableResource<EmcInternalAuditService, EmcInternalAuditBean, Long> {
}
