/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationAudioBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationAudioDao;
import com.sunwayworld.module.item.notification.mapper.CoreNotificationAudioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNotificationAudioDaoImpl
extends MybatisDaoSupport<CoreNotificationAudioBean, Long>
implements CoreNotificationAudioDao {
    @Autowired
    private CoreNotificationAudioMapper coreNotificationAudioMapper;

    public CoreNotificationAudioMapper getMapper() {
        return this.coreNotificationAudioMapper;
    }
}
