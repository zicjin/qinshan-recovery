/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.wechat.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.wechat.bean.CoreWechatUserBean;
import com.sunwayworld.module.sys.wechat.dao.CoreWechatUserDao;
import com.sunwayworld.module.sys.wechat.mapper.CoreWechatUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreWechatUserDaoImpl
extends MybatisDaoSupport<CoreWechatUserBean, String>
implements CoreWechatUserDao {
    @Autowired
    private CoreWechatUserMapper userMapper;

    public CoreWechatUserMapper getMapper() {
        return this.userMapper;
    }
}
