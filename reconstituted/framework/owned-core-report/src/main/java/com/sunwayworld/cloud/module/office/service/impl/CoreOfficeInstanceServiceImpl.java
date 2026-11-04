/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.service.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeInstanceBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeInstanceDao;
import com.sunwayworld.cloud.module.office.service.CoreOfficeInstanceService;
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
public class CoreOfficeInstanceServiceImpl
implements CoreOfficeInstanceService {
    @Autowired
    private CoreOfficeInstanceDao coreOfficeInstanceDao;

    @Override
    public CoreOfficeInstanceDao getDao() {
        return this.coreOfficeInstanceDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreOfficeInstanceBean coreOfficeInstance = jsonWrapper.parseUnique(CoreOfficeInstanceBean.class);
        coreOfficeInstance.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreOfficeInstance);
        return coreOfficeInstance.getId();
    }
}
