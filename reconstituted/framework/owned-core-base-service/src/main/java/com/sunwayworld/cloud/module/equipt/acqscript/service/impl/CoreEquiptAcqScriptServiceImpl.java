/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqscript.service.impl;

import com.sunwayworld.cloud.module.equipt.acqscript.bean.CoreEquiptAcqScriptBean;
import com.sunwayworld.cloud.module.equipt.acqscript.persistent.dao.CoreEquiptAcqScriptDao;
import com.sunwayworld.cloud.module.equipt.acqscript.service.CoreEquiptAcqScriptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreEquiptAcqScriptServiceImpl
implements CoreEquiptAcqScriptService {
    @Autowired
    private CoreEquiptAcqScriptDao coreEquiptAcqScriptDao;

    @Override
    public CoreEquiptAcqScriptDao getDao() {
        return this.coreEquiptAcqScriptDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean jsonWrapper) {
        CoreEquiptAcqScriptBean coreEquiptAcqScript = jsonWrapper.parseUnique(CoreEquiptAcqScriptBean.class);
        coreEquiptAcqScript.setId(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
        this.getDao().insert(coreEquiptAcqScript);
        return coreEquiptAcqScript.getId();
    }
}
