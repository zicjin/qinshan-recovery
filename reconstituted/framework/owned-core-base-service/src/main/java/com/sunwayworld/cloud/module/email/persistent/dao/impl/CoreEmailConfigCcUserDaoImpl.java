/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcUserBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigCcUserDao;
import com.sunwayworld.cloud.module.email.persistent.mapper.CoreEmailConfigCcUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEmailConfigCcUserDaoImpl
extends MybatisDaoSupport<CoreEmailConfigCcUserBean, Long>
implements CoreEmailConfigCcUserDao {
    @Autowired
    private CoreEmailConfigCcUserMapper coreEmailConfigCcUserMapper;

    public CoreEmailConfigCcUserMapper getMapper() {
        return this.coreEmailConfigCcUserMapper;
    }
}
