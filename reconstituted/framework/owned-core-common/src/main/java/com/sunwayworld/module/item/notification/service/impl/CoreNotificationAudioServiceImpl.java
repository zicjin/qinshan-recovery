/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationAudioBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationAudioDao;
import com.sunwayworld.module.item.notification.service.CoreNotificationAudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreNotificationAudioServiceImpl
implements CoreNotificationAudioService {
    @Autowired
    private CoreNotificationAudioDao coreNotificationAudioDao;

    @Override
    public CoreNotificationAudioDao getDao() {
        return this.coreNotificationAudioDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreNotificationAudioBean coreNotificationAudio = jsonWrapper.parseUnique(CoreNotificationAudioBean.class);
        coreNotificationAudio.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreNotificationAudio);
        return coreNotificationAudio.getId();
    }
}
