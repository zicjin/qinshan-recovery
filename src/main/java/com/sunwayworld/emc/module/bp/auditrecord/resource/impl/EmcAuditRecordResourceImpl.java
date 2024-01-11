package com.sunwayworld.emc.module.bp.auditrecord.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.auditrecord.resource.EmcAuditRecordResource;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.framework.log.annotation.LogModule;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-07
 */

@LogModule("审核记录日志表")
@RestController
public class EmcAuditRecordResourceImpl implements EmcAuditRecordResource {
    @Autowired
    private EmcAuditRecordService auditRecordService;

    @Override
    public EmcAuditRecordService getService() {
        return auditRecordService;
    }

}
