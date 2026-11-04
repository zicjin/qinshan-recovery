/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceToBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationInstanceToDao;
import com.sunwayworld.module.item.notification.mapper.CoreNotificationInstanceToMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreNotificationInstanceToDaoImpl
extends MybatisDaoSupport<CoreNotificationInstanceToBean, Long>
implements CoreNotificationInstanceToDao {
    @Autowired
    private CoreNotificationInstanceToMapper instanceToMapper;

    public CoreNotificationInstanceToMapper getMapper() {
        return this.instanceToMapper;
    }
}
