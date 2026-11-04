/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.dailysupervision.service.impl;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionItemBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.persistent.dao.CoreDailySupervisionItemDao;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionItemService;
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
public class CoreDailySupervisionItemServiceImpl
implements CoreDailySupervisionItemService {
    @Autowired
    private CoreDailySupervisionItemDao coreDailySupervisionItemDao;

    @Override
    public CoreDailySupervisionItemDao getDao() {
        return this.coreDailySupervisionItemDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreDailySupervisionItemBean coreDailySupervisionItem = jsonWrapper.parseUnique(CoreDailySupervisionItemBean.class);
        coreDailySupervisionItem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreDailySupervisionItem);
        return coreDailySupervisionItem.getId();
    }
}
