/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTraceOverviewDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.dao.CoreAuditTrailRecordDao;
import com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditTrailRecordDaoImpl
extends MybatisDaoSupport<CoreAuditTrailRecordBean, Long>
implements CoreAuditTrailRecordDao {
    @Autowired
    private CoreAuditTrailRecordMapper recordMapper;

    public CoreAuditTrailRecordMapper getMapper() {
        return this.recordMapper;
    }

    @Override
    public List<CoreAuditTraceOverviewDTO> selectTraceOverview(String targetId, String tableName, String nameField) {
        return this.getMapper().selectTraceOverview(targetId, tableName, nameField, null);
    }
    @Override
    public List<CoreAuditTraceOverviewDTO> selectTraceOverview(String targetId, String tableName, String nameField, String organization) {
        return this.getMapper().selectTraceOverview(targetId, tableName, nameField, organization);
    }
}
