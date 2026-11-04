/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.internalaudit.persistent.dao.CoreInternalAuditGroupDao;
import com.sunwayworld.cloud.module.qms.internalaudit.persistent.mapper.CoreInternalAuditGroupMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreInternalAuditGroupDaoImpl
extends MybatisDaoSupport<CoreInternalAuditGroupBean, Long>
implements CoreInternalAuditGroupDao {
    @Autowired
    private CoreInternalAuditGroupMapper coreInternalAuditGroupMapper;

    public CoreInternalAuditGroupMapper getMapper() {
        return this.coreInternalAuditGroupMapper;
    }
}
