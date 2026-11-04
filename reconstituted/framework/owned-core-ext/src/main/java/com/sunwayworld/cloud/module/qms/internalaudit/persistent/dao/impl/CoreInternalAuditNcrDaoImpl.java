/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditNcrBean;
import com.sunwayworld.cloud.module.qms.internalaudit.persistent.dao.CoreInternalAuditNcrDao;
import com.sunwayworld.cloud.module.qms.internalaudit.persistent.mapper.CoreInternalAuditNcrMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreInternalAuditNcrDaoImpl
extends MybatisDaoSupport<CoreInternalAuditNcrBean, Long>
implements CoreInternalAuditNcrDao {
    @Autowired
    private CoreInternalAuditNcrMapper coreInternalAuditNcrMapper;

    public CoreInternalAuditNcrMapper getMapper() {
        return this.coreInternalAuditNcrMapper;
    }
}
