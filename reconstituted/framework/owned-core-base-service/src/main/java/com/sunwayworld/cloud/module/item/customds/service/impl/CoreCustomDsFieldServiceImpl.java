/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.service.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsFieldBean;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsFieldDao;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsFieldService;
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
public class CoreCustomDsFieldServiceImpl
implements CoreCustomDsFieldService {
    @Autowired
    private CoreCustomDsFieldDao coreCustomDsFieldDao;

    @Override
    public CoreCustomDsFieldDao getDao() {
        return this.coreCustomDsFieldDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomDsFieldBean coreCustomDsField = jsonWrapper.parseUnique(CoreCustomDsFieldBean.class);
        coreCustomDsField.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreCustomDsField);
        return coreCustomDsField.getId();
    }
}
