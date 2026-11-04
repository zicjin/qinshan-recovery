/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogActivityBean;
import java.util.List;
import java.util.Map;

public interface CoreAuditLogActivityDao
extends GenericDao<CoreAuditLogActivityBean, Long> {
    public List<Map<String, Object>> selectAuditLogActivityByCondition(MapperParameter var1);
}
