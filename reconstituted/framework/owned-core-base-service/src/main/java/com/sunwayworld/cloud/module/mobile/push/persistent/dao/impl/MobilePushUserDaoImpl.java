/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.persistent.dao.impl;

import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushUserBean;
import com.sunwayworld.cloud.module.mobile.push.persistent.dao.MobilePushUserDao;
import com.sunwayworld.cloud.module.mobile.push.persistent.mapper.MobilePushUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class MobilePushUserDaoImpl
extends MybatisDaoSupport<MobilePushUserBean, String>
implements MobilePushUserDao {
    @Autowired
    private MobilePushUserMapper mobilePushUserMapper;

    public MobilePushUserMapper getMapper() {
        return this.mobilePushUserMapper;
    }
}
