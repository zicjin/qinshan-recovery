/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.service.impl;

import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemBean;
import com.sunwayworld.cloud.module.nfssi.report.persistent.dao.CoreNfssiReportItemDao;
import com.sunwayworld.cloud.module.nfssi.report.service.CoreNfssiReportItemService;
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
public class CoreNfssiReportItemServiceImpl
implements CoreNfssiReportItemService {
    @Autowired
    private CoreNfssiReportItemDao coreNfssiReportItemDao;

    @Override
    public CoreNfssiReportItemDao getDao() {
        return this.coreNfssiReportItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreNfssiReportItemBean coreNfssiReportItem = jsonWrapper.parseUnique(CoreNfssiReportItemBean.class);
        coreNfssiReportItem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreNfssiReportItem);
        return coreNfssiReportItem.getId();
    }
}
