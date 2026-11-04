/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcRoleBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigCcRoleDao;
import com.sunwayworld.cloud.module.email.persistent.mapper.CoreEmailConfigCcRoleMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEmailConfigCcRoleDaoImpl
extends MybatisDaoSupport<CoreEmailConfigCcRoleBean, Long>
implements CoreEmailConfigCcRoleDao {
    @Autowired
    private CoreEmailConfigCcRoleMapper coreEmailConfigCcRoleMapper;

    public CoreEmailConfigCcRoleMapper getMapper() {
        return this.coreEmailConfigCcRoleMapper;
    }

    @Override
    public List<String> selectUserListByConfig(String configId) {
        return this.getMapper().selectUserListByConfig(configId);
    }
}
