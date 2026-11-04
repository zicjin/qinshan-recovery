/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.service.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderTestItemBean;
import com.sunwayworld.cloud.module.nfssi.order.persistent.dao.CoreNfssiOrderTestItemDao;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiOrderTestItemService;
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
public class CoreNfssiOrderTestItemServiceImpl
implements CoreNfssiOrderTestItemService {
    @Autowired
    private CoreNfssiOrderTestItemDao coreNfssiOrderTestItemDao;

    @Override
    public CoreNfssiOrderTestItemDao getDao() {
        return this.coreNfssiOrderTestItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreNfssiOrderTestItemBean coreNfssiOrderTestItem = jsonWrapper.parseUnique(CoreNfssiOrderTestItemBean.class);
        coreNfssiOrderTestItem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreNfssiOrderTestItem);
        return coreNfssiOrderTestItem.getId();
    }
}
