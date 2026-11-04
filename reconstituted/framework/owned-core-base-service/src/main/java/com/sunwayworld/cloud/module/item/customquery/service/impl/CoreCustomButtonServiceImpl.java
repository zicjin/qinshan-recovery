/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.service.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomButtonBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomButtonDao;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomButtonService;
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
public class CoreCustomButtonServiceImpl
implements CoreCustomButtonService {
    @Autowired
    private CoreCustomButtonDao coreCustomButtonDao;

    @Override
    public CoreCustomButtonDao getDao() {
        return this.coreCustomButtonDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomButtonBean coreCustomButton = jsonWrapper.parseUnique(CoreCustomButtonBean.class);
        coreCustomButton.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreCustomButton);
        return coreCustomButton.getId();
    }
}
