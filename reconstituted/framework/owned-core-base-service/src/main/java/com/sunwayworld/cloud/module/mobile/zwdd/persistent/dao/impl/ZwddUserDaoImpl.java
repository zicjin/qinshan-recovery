/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.persistent.dao.impl;

import com.sunwayworld.cloud.module.mobile.zwdd.bean.ZwddUserBean;
import com.sunwayworld.cloud.module.mobile.zwdd.persistent.dao.ZwddUserDao;
import com.sunwayworld.cloud.module.mobile.zwdd.persistent.mapper.ZwddUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class ZwddUserDaoImpl
extends MybatisDaoSupport<ZwddUserBean, String>
implements ZwddUserDao {
    @Autowired
    private ZwddUserMapper zwddUserMapper;

    public ZwddUserMapper getMapper() {
        return this.zwddUserMapper;
    }
}
