/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.importrecord.resource.impl;

import com.sunwayworld.cloud.module.lcdp.importrecord.bean.LcdpResourceImportRecordBean;
import com.sunwayworld.cloud.module.lcdp.importrecord.resource.LcdpResourceImportRecordResource;
import com.sunwayworld.cloud.module.lcdp.importrecord.service.LcdpResourceImportRecordService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5bfc\u5165\u8bb0\u5f55\u8868")
@RestController
@GikamBean
public class LcdpResourceImportRecordResourceImpl
implements LcdpResourceImportRecordResource,
AbstractGenericResource<LcdpResourceImportRecordService, LcdpResourceImportRecordBean, Long> {
    @Autowired
    private LcdpResourceImportRecordService lcdpResourceImportRecordService;

    @Override
    public LcdpResourceImportRecordService getService() {
        return this.lcdpResourceImportRecordService;
    }
}
