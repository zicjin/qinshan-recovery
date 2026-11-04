/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationTemplateBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationTemplateDao;
import com.sunwayworld.module.item.notification.mapper.CoreNotificationTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreNotificationTemplateDaoImpl
extends MybatisDaoSupport<CoreNotificationTemplateBean, String>
implements CoreNotificationTemplateDao {
    @Autowired
    private CoreNotificationTemplateMapper templateMapper;

    public CoreNotificationTemplateMapper getMapper() {
        return this.templateMapper;
    }
}
