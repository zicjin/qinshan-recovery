/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineNameDTO;
import com.sunwayworld.module.sys.at.dao.CoreAuditTrailRecordLineDao;
import com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordLineMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditTrailRecordLineDaoImpl
extends MybatisDaoSupport<CoreAuditTrailRecordLineBean, Long>
implements CoreAuditTrailRecordLineDao {
    @Autowired
    private CoreAuditTrailRecordLineMapper recordLineMapper;

    public CoreAuditTrailRecordLineMapper getMapper() {
        return this.recordLineMapper;
    }

    @Override
    public List<String> selectTableNameByRecordId(String recordId) {
        return this.getMapper().selectTableNameByRecordId(recordId);
    }

    @Override
    public List<CoreAuditTrailRecordLineNameDTO> selectDeletedNames(List<String> targetIds, String nameField) {
        return this.getMapper().selectDeletedNames(targetIds, nameField);
    }
}
