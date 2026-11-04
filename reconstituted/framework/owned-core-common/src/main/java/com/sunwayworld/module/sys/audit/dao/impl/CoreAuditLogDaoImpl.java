/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogTraceOverviewDTO;
import com.sunwayworld.module.sys.audit.dao.CoreAuditLogDao;
import com.sunwayworld.module.sys.audit.mapper.CoreAuditLogMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditLogDaoImpl
extends MybatisDaoSupport<CoreAuditLogBean, Long>
implements CoreAuditLogDao {
    @Autowired
    private CoreAuditLogMapper logMapper;

    public CoreAuditLogMapper getMapper() {
        return this.logMapper;
    }

    @Override
    public List<CoreAuditLogTraceOverviewDTO> selectTraceOverview(String targetId, String tableName, String nameField) {
        return this.getMapper().selectTraceOverview(targetId, tableName, nameField);
    }
}
