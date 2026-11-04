/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogActivityBean;
import com.sunwayworld.module.sys.audit.dao.CoreAuditLogActivityDao;
import com.sunwayworld.module.sys.audit.mapper.CoreAuditLogActivityMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditLogActivityDaoImpl
extends MybatisDaoSupport<CoreAuditLogActivityBean, Long>
implements CoreAuditLogActivityDao {
    @Autowired
    private CoreAuditLogActivityMapper logActivityMapper;

    public CoreAuditLogActivityMapper getMapper() {
        return this.logActivityMapper;
    }

    @Override
    public List<Map<String, Object>> selectAuditLogActivityByCondition(MapperParameter parameter) {
        return this.logActivityMapper.selectAuditLogActivityByCondition(parameter);
    }
}
