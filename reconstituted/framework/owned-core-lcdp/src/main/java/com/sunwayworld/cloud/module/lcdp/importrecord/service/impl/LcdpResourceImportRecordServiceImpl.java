/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.importrecord.service.impl;

import com.sunwayworld.cloud.module.lcdp.importrecord.persistent.dao.LcdpResourceImportRecordDao;
import com.sunwayworld.cloud.module.lcdp.importrecord.service.LcdpResourceImportRecordService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpResourceImportRecordServiceImpl
implements LcdpResourceImportRecordService {
    @Autowired
    private LcdpResourceImportRecordDao lcdpResourceImportRecordDao;

    @Override
    public LcdpResourceImportRecordDao getDao() {
        return this.lcdpResourceImportRecordDao;
    }
}
