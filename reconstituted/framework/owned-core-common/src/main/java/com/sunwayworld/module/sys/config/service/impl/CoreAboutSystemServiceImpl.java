/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.config.bean.CoreAboutSystemBean;
import com.sunwayworld.module.sys.config.dao.CoreAboutSystemDao;
import com.sunwayworld.module.sys.config.service.CoreAboutSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreAboutSystemServiceImpl
implements CoreAboutSystemService {
    @Autowired
    private CoreAboutSystemDao coreAboutSystemDao;

    @Override
    public CoreAboutSystemDao getDao() {
        return this.coreAboutSystemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreAboutSystemBean aboutSystem = wrapper.parseUnique(CoreAboutSystemBean.class);
        aboutSystem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(aboutSystem);
        return aboutSystem.getId();
    }
}
