/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcUserBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigCcUserDao;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigCcUserService;
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
public class CoreEmailConfigCcUserServiceImpl
implements CoreEmailConfigCcUserService {
    @Autowired
    private CoreEmailConfigCcUserDao coreEmailConfigCcUserDao;

    @Override
    public CoreEmailConfigCcUserDao getDao() {
        return this.coreEmailConfigCcUserDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreEmailConfigCcUserBean coreEmailConfigCcUser = jsonWrapper.parseUnique(CoreEmailConfigCcUserBean.class);
        coreEmailConfigCcUser.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreEmailConfigCcUser);
        return coreEmailConfigCcUser.getId();
    }
}
