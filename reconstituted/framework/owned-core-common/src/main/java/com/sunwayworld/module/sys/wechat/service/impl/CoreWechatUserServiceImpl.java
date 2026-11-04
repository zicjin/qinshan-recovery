/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.wechat.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.wechat.bean.CoreWechatUserBean;
import com.sunwayworld.module.sys.wechat.dao.CoreWechatUserDao;
import com.sunwayworld.module.sys.wechat.service.CoreWechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreWechatUserServiceImpl
implements CoreWechatUserService {
    @Autowired
    private CoreWechatUserDao userDao;

    @Override
    public CoreWechatUserDao getDao() {
        return this.userDao;
    }

    @Override
    @Transactional
    public void insert(String userId, String openId) {
        CoreWechatUserBean user = new CoreWechatUserBean();
        user.setId(userId);
        user.setOpenId(openId);
        this.getDao().insert(user);
    }
}
