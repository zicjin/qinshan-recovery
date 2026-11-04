/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.notice.service.impl;

import com.sunwayworld.cloud.module.item.notice.persistent.dao.CoreNoticePermissionDao;
import com.sunwayworld.cloud.module.item.notice.service.CoreNoticePermissionService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNoticePermissionServiceImpl
implements CoreNoticePermissionService {
    @Autowired
    private CoreNoticePermissionDao noticePermissionDao;

    @Override
    public CoreNoticePermissionDao getDao() {
        return this.noticePermissionDao;
    }
}
