/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.service.impl;

import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryBean;
import com.sunwayworld.cloud.module.qms.filetype.persistent.dao.CoreFileCategoryDao;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryService;
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
public class CoreFileCategoryServiceImpl
implements CoreFileCategoryService {
    @Autowired
    private CoreFileCategoryDao coreFileCategoryDao;

    @Override
    public CoreFileCategoryDao getDao() {
        return this.coreFileCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreFileCategoryBean coreFileCategory = jsonWrapper.parseUnique(CoreFileCategoryBean.class);
        coreFileCategory.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreFileCategory);
        return coreFileCategory.getId();
    }
}
