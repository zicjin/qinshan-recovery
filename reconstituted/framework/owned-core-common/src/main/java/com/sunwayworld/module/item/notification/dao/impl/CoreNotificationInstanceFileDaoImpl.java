/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceFileBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationInstanceFileDao;
import com.sunwayworld.module.item.notification.mapper.CoreNotificationInstanceFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreNotificationInstanceFileDaoImpl
extends MybatisDaoSupport<CoreNotificationInstanceFileBean, Long>
implements CoreNotificationInstanceFileDao {
    @Autowired
    private CoreNotificationInstanceFileMapper instanceFileMapper;

    public CoreNotificationInstanceFileMapper getMapper() {
        return this.instanceFileMapper;
    }
}
