/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.at.dao.CoreAuditTrailConfigLineDao;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailConfigLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditTrailConfigLineServiceImpl
implements CoreAuditTrailConfigLineService {
    @Autowired
    private CoreAuditTrailConfigLineDao configLineDao;

    @Override
    public CoreAuditTrailConfigLineDao getDao() {
        return this.configLineDao;
    }
}
