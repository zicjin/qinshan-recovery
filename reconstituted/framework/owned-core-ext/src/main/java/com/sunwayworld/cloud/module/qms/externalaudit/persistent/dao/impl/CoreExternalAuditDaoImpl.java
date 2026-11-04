/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditBean;
import com.sunwayworld.cloud.module.qms.externalaudit.persistent.dao.CoreExternalAuditDao;
import com.sunwayworld.cloud.module.qms.externalaudit.persistent.mapper.CoreExternalAuditMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreExternalAuditDaoImpl
extends MybatisDaoSupport<CoreExternalAuditBean, Long>
implements CoreExternalAuditDao {
    @Autowired
    private CoreExternalAuditMapper coreExternalAuditMapper;

    public CoreExternalAuditMapper getMapper() {
        return this.coreExternalAuditMapper;
    }
}
