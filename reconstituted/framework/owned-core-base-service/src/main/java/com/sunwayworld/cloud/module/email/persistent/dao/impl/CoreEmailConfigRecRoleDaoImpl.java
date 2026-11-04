/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigRecRoleBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigRecRoleDao;
import com.sunwayworld.cloud.module.email.persistent.mapper.CoreEmailConfigRecRoleMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEmailConfigRecRoleDaoImpl
extends MybatisDaoSupport<CoreEmailConfigRecRoleBean, Long>
implements CoreEmailConfigRecRoleDao {
    @Autowired
    private CoreEmailConfigRecRoleMapper coreEmailConfigRecRoleMapper;

    public CoreEmailConfigRecRoleMapper getMapper() {
        return this.coreEmailConfigRecRoleMapper;
    }

    @Override
    public List<String> selectUserListByConfig(String configId) {
        return this.getMapper().selectUserListByConfig(configId);
    }
}
