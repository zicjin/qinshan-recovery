/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.externalaudit.persistent.dao.CoreExternalAuditGroupDao;
import com.sunwayworld.cloud.module.qms.externalaudit.persistent.mapper.CoreExternalAuditGroupMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreExternalAuditGroupDaoImpl
extends MybatisDaoSupport<CoreExternalAuditGroupBean, Long>
implements CoreExternalAuditGroupDao {
    @Autowired
    private CoreExternalAuditGroupMapper coreExternalAuditGroupMapper;

    public CoreExternalAuditGroupMapper getMapper() {
        return this.coreExternalAuditGroupMapper;
    }
}
