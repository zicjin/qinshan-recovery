/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.notice.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticePermissionBean;
import com.sunwayworld.cloud.module.item.notice.persistent.dao.CoreNoticePermissionDao;
import com.sunwayworld.cloud.module.item.notice.persistent.mapper.CoreNoticePermissionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNoticePermissionDaoImpl
extends MybatisDaoSupport<CoreNoticePermissionBean, Long>
implements CoreNoticePermissionDao {
    @Autowired
    private CoreNoticePermissionMapper noticePermissionMapper;

    public CoreNoticePermissionMapper getMapper() {
        return this.noticePermissionMapper;
    }
}
