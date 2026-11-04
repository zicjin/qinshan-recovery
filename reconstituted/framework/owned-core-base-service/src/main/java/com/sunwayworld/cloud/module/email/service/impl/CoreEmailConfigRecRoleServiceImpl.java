/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service.impl;

import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigRecRoleDao;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigRecRoleService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEmailConfigRecRoleServiceImpl
implements CoreEmailConfigRecRoleService {
    @Autowired
    private CoreEmailConfigRecRoleDao coreEmailConfigRecRoleDao;

    @Override
    public CoreEmailConfigRecRoleDao getDao() {
        return this.coreEmailConfigRecRoleDao;
    }

    @Override
    public List<String> selectUserListByConfig(String configId) {
        return this.getDao().selectUserListByConfig(configId);
    }
}
