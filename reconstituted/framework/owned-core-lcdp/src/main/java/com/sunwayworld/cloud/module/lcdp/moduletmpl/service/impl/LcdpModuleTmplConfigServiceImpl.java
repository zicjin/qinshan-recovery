/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.service.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplConfigBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpModuleTmplConfigDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpModuleTmplConfigService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpModuleTmplConfigServiceImpl
implements LcdpModuleTmplConfigService {
    @Autowired
    private LcdpModuleTmplConfigDao lcdpModuleTmplConfigDao;

    @Override
    public LcdpModuleTmplConfigDao getDao() {
        return this.lcdpModuleTmplConfigDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LcdpModuleTmplConfigBean lcdpModuleTmplConfig = jsonWrapper.parseUnique(LcdpModuleTmplConfigBean.class);
        lcdpModuleTmplConfig.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(lcdpModuleTmplConfig);
        return lcdpModuleTmplConfig.getId();
    }
}
