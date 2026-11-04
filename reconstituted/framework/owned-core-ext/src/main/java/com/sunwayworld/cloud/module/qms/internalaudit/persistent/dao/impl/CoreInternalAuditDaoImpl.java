/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditBean;
import com.sunwayworld.cloud.module.qms.internalaudit.persistent.dao.CoreInternalAuditDao;
import com.sunwayworld.cloud.module.qms.internalaudit.persistent.mapper.CoreInternalAuditMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreInternalAuditDaoImpl
extends MybatisDaoSupport<CoreInternalAuditBean, Long>
implements CoreInternalAuditDao {
    @Autowired
    private CoreInternalAuditMapper coreInternalAuditMapper;

    public CoreInternalAuditMapper getMapper() {
        return this.coreInternalAuditMapper;
    }
}
