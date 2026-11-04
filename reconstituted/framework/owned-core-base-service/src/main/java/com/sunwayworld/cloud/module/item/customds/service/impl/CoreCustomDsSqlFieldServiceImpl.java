/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.service.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlFieldBean;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsSqlFieldDao;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsSqlFieldService;
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
public class CoreCustomDsSqlFieldServiceImpl
implements CoreCustomDsSqlFieldService {
    @Autowired
    private CoreCustomDsSqlFieldDao coreCustomDsSqlFieldDao;

    @Override
    public CoreCustomDsSqlFieldDao getDao() {
        return this.coreCustomDsSqlFieldDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomDsSqlFieldBean coreCustomDsSqlField = jsonWrapper.parseUnique(CoreCustomDsSqlFieldBean.class);
        coreCustomDsSqlField.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreCustomDsSqlField);
        return coreCustomDsSqlField.getId();
    }
}
