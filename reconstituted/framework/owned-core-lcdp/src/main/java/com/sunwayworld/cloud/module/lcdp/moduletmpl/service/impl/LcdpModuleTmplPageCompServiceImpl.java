/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.service.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplPageCompBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpModuleTmplPageCompDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpModuleTmplPageCompService;
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
public class LcdpModuleTmplPageCompServiceImpl
implements LcdpModuleTmplPageCompService {
    @Autowired
    private LcdpModuleTmplPageCompDao lcdpModuleTmplPageCompDao;

    @Override
    public LcdpModuleTmplPageCompDao getDao() {
        return this.lcdpModuleTmplPageCompDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        LcdpModuleTmplPageCompBean lcdpModuleTmplPageComp = jsonWrapper.parseUnique(LcdpModuleTmplPageCompBean.class);
        lcdpModuleTmplPageComp.setId(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
        this.getDao().insert(lcdpModuleTmplPageComp);
        return lcdpModuleTmplPageComp.getId();
    }
}
