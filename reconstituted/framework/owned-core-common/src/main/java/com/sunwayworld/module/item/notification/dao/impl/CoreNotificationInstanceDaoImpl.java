/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationInstanceDao;
import com.sunwayworld.module.item.notification.mapper.CoreNotificationInstanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreNotificationInstanceDaoImpl
extends MybatisDaoSupport<CoreNotificationInstanceBean, Long>
implements CoreNotificationInstanceDao {
    @Autowired
    private CoreNotificationInstanceMapper instanceMapper;

    public CoreNotificationInstanceMapper getMapper() {
        return this.instanceMapper;
    }
}
