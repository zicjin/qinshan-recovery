/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.service.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsFieldBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeDsFieldDao;
import com.sunwayworld.cloud.module.office.service.CoreOfficeDsFieldService;
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
public class CoreOfficeDsFieldServiceImpl
implements CoreOfficeDsFieldService {
    @Autowired
    private CoreOfficeDsFieldDao coreOfficeDsFieldDao;

    @Override
    public CoreOfficeDsFieldDao getDao() {
        return this.coreOfficeDsFieldDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreOfficeDsFieldBean coreOfficeDsField = jsonWrapper.parseUnique(CoreOfficeDsFieldBean.class);
        coreOfficeDsField.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreOfficeDsField);
        return coreOfficeDsField.getId();
    }
}
