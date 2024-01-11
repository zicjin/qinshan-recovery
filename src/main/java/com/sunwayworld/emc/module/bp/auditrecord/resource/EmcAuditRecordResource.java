package com.sunwayworld.emc.module.bp.auditrecord.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-07
 */
@RequestMapping("/secure/emc/module/bp/audit-records")
public interface EmcAuditRecordResource extends GenericResource<EmcAuditRecordService, EmcAuditRecordBean, Long> {
}
