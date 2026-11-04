/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.service.impl;

import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpServerScriptMethodDao;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpServerScriptMethodService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpServerScriptMethodServicempl
implements LcdpServerScriptMethodService {
    @Autowired
    private LcdpServerScriptMethodDao lcdpServerScriptMethodDao;

    @Override
    public LcdpServerScriptMethodDao getDao() {
        return this.lcdpServerScriptMethodDao;
    }
}
