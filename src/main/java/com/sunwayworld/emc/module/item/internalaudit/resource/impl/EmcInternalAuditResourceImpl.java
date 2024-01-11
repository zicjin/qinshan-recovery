package com.sunwayworld.emc.module.item.internalaudit.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.internalaudit.resource.EmcInternalAuditResource;
import com.sunwayworld.emc.module.item.internalaudit.service.EmcInternalAuditService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

@LogModule("日常请假表")
@RestController
@GikamBean
public class EmcInternalAuditResourceImpl implements EmcInternalAuditResource {
    @Autowired
    private EmcInternalAuditService internalAuditService;

    @Override
    public EmcInternalAuditService getService() {
        return internalAuditService;
    }
}
