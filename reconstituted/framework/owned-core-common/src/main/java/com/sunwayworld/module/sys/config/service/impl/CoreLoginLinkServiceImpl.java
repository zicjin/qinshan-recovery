/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.config.bean.CoreLoginLinkBean;
import com.sunwayworld.module.sys.config.dao.CoreLoginLinkDao;
import com.sunwayworld.module.sys.config.service.CoreLoginLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreLoginLinkServiceImpl
implements CoreLoginLinkService {
    @Autowired
    private CoreLoginLinkDao coreLoginLinkDao;

    @Override
    public CoreLoginLinkDao getDao() {
        return this.coreLoginLinkDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreLoginLinkBean coreLoginLink = jsonWrapper.parseUnique(CoreLoginLinkBean.class);
        coreLoginLink.setId(ApplicationContextHelper.getNextIdentity());
        coreLoginLink.setOrderNo(coreLoginLink.getId());
        this.getDao().insert(coreLoginLink);
        return coreLoginLink.getId();
    }
}
