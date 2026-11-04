/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.service.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageReadRecordBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManageReadRecordDao;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageReadRecordService;
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
public class CoreFileManageReadRecordServiceImpl
implements CoreFileManageReadRecordService {
    @Autowired
    private CoreFileManageReadRecordDao coreFileManageReadRecordDao;

    @Override
    public CoreFileManageReadRecordDao getDao() {
        return this.coreFileManageReadRecordDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreFileManageReadRecordBean coreFileManageReadRecord = jsonWrapper.parseUnique(CoreFileManageReadRecordBean.class);
        coreFileManageReadRecord.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreFileManageReadRecord);
        return coreFileManageReadRecord.getId();
    }
}
