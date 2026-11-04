/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnNameDTO;
import com.sunwayworld.module.sys.audit.dao.CoreAuditLogColumnDao;
import com.sunwayworld.module.sys.audit.mapper.CoreAuditLogColumnMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditLogColumnDaoImpl
extends MybatisDaoSupport<CoreAuditLogColumnBean, Long>
implements CoreAuditLogColumnDao {
    @Autowired
    private CoreAuditLogColumnMapper logColumnMapper;

    public CoreAuditLogColumnMapper getMapper() {
        return this.logColumnMapper;
    }

    @Override
    public List<CoreAuditLogColumnNameDTO> selectDeletedNames(List<String> targetIds, String tableName, String nameField) {
        return this.getMapper().selectDeletedNames(targetIds, tableName, nameField);
    }

    @Override
    public List<CoreAuditLogColumnDTO> selectColumnsByTargetInfo(String tableName, String targetId) {
        return this.getMapper().selectColumnsByTargetInfo(tableName, targetId);
    }
}
