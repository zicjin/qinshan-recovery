/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.prevention.service.impl;

import com.sunwayworld.cloud.module.qms.prevention.bean.CorePreventionItemBean;
import com.sunwayworld.cloud.module.qms.prevention.persistent.dao.CorePreventionItemDao;
import com.sunwayworld.cloud.module.qms.prevention.service.CorePreventionItemService;
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
public class CorePreventionItemServiceImpl
implements CorePreventionItemService {
    @Autowired
    private CorePreventionItemDao corePreventionItemDao;

    @Override
    public CorePreventionItemDao getDao() {
        return this.corePreventionItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CorePreventionItemBean corePreventionItem = jsonWrapper.parseUnique(CorePreventionItemBean.class);
        corePreventionItem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(corePreventionItem);
        return corePreventionItem.getId();
    }
}
