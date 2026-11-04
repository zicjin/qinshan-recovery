/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.service.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryFieldBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomQueryFieldDao;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomQueryFieldService;
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
public class CoreCustomQueryFieldServiceImpl
implements CoreCustomQueryFieldService {
    @Autowired
    private CoreCustomQueryFieldDao coreCustomQueryFieldDao;

    @Override
    public CoreCustomQueryFieldDao getDao() {
        return this.coreCustomQueryFieldDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomQueryFieldBean coreCustomQueryField = jsonWrapper.parseUnique(CoreCustomQueryFieldBean.class);
        coreCustomQueryField.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreCustomQueryField);
        return coreCustomQueryField.getId();
    }
}
