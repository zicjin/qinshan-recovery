/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditNcrBean;
import com.sunwayworld.cloud.module.qms.externalaudit.persistent.dao.CoreExternalAuditNcrDao;
import com.sunwayworld.cloud.module.qms.externalaudit.persistent.mapper.CoreExternalAuditNcrMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreExternalAuditNcrDaoImpl
extends MybatisDaoSupport<CoreExternalAuditNcrBean, Long>
implements CoreExternalAuditNcrDao {
    @Autowired
    private CoreExternalAuditNcrMapper coreExternalAuditNcrMapper;

    public CoreExternalAuditNcrMapper getMapper() {
        return this.coreExternalAuditNcrMapper;
    }
}
