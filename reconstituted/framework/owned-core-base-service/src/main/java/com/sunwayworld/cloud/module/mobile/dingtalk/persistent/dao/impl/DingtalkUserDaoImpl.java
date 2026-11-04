/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.persistent.dao.impl;

import com.sunwayworld.cloud.module.mobile.dingtalk.bean.DingtalkUserBean;
import com.sunwayworld.cloud.module.mobile.dingtalk.persistent.dao.DingtalkUserDao;
import com.sunwayworld.cloud.module.mobile.dingtalk.persistent.mapper.DingtalkUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class DingtalkUserDaoImpl
extends MybatisDaoSupport<DingtalkUserBean, String>
implements DingtalkUserDao {
    @Autowired
    private DingtalkUserMapper dingtalkUserMapper;

    public DingtalkUserMapper getMapper() {
        return this.dingtalkUserMapper;
    }
}
