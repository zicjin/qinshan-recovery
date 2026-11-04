/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.persistent.dao.impl;

import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushMessageBean;
import com.sunwayworld.cloud.module.mobile.push.persistent.dao.MobilePushMessageDao;
import com.sunwayworld.cloud.module.mobile.push.persistent.mapper.MobilePushMessageMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class MobilePushMessageDaoImpl
extends MybatisDaoSupport<MobilePushMessageBean, Long>
implements MobilePushMessageDao {
    @Autowired
    private MobilePushMessageMapper mobilePushmessageMapper;

    public MobilePushMessageMapper getMapper() {
        return this.mobilePushmessageMapper;
    }
}
