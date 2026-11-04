/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.service.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManagePrintRecorBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManagePrintRecorDao;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManagePrintRecorService;
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
public class CoreFileManagePrintRecorServiceImpl
implements CoreFileManagePrintRecorService {
    @Autowired
    private CoreFileManagePrintRecorDao coreFileManagePrintRecorDao;

    @Override
    public CoreFileManagePrintRecorDao getDao() {
        return this.coreFileManagePrintRecorDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreFileManagePrintRecorBean coreFileManagePrintRecor = jsonWrapper.parseUnique(CoreFileManagePrintRecorBean.class);
        coreFileManagePrintRecor.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreFileManagePrintRecor);
        return coreFileManagePrintRecor.getId();
    }
}
