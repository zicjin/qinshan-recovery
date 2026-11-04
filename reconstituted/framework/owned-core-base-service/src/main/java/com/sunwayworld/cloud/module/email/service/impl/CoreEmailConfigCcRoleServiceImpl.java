/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcRoleBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigCcRoleDao;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigCcRoleService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreEmailConfigCcRoleServiceImpl
implements CoreEmailConfigCcRoleService {
    @Autowired
    private CoreEmailConfigCcRoleDao coreEmailConfigCcRoleDao;

    @Override
    public CoreEmailConfigCcRoleDao getDao() {
        return this.coreEmailConfigCcRoleDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreEmailConfigCcRoleBean coreEmailConfigCcRole = jsonWrapper.parseUnique(CoreEmailConfigCcRoleBean.class);
        coreEmailConfigCcRole.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreEmailConfigCcRole);
        return coreEmailConfigCcRole.getId();
    }

    @Override
    public List<String> selectUserListByConfig(String configId) {
        return this.getDao().selectUserListByConfig(configId);
    }
}
