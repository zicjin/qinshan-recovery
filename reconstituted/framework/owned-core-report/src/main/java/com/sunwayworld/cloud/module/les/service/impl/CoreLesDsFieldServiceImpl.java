/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesDsFieldBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesDsFieldDao;
import com.sunwayworld.cloud.module.les.service.CoreLesDsFieldService;
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
public class CoreLesDsFieldServiceImpl
implements CoreLesDsFieldService {
    @Autowired
    private CoreLesDsFieldDao coreLesDsFieldDao;

    @Override
    public CoreLesDsFieldDao getDao() {
        return this.coreLesDsFieldDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreLesDsFieldBean lesDsField = wrapper.parseUnique(CoreLesDsFieldBean.class);
        lesDsField.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(lesDsField);
        return lesDsField.getId();
    }
}
