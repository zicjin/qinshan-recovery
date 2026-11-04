/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.ncr.service.impl;

import com.sunwayworld.cloud.module.qms.ncr.bean.CoreNcrBean;
import com.sunwayworld.cloud.module.qms.ncr.persistent.dao.CoreNcrDao;
import com.sunwayworld.cloud.module.qms.ncr.service.CoreNcrService;
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
public class CoreNcrServiceImpl
implements CoreNcrService {
    @Autowired
    private CoreNcrDao coreNcrDao;

    @Override
    public CoreNcrDao getDao() {
        return this.coreNcrDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreNcrBean coreNcr = jsonWrapper.parseUnique(CoreNcrBean.class);
        coreNcr.setId(ApplicationContextHelper.getNextIdentity());
        coreNcr.setNcrNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "BFHX"));
        this.getDao().insert(coreNcr);
        return coreNcr.getId();
    }
}
