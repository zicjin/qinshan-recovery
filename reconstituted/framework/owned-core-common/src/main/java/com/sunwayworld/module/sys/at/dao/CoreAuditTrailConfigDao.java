/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigBean;
import java.util.List;

public interface CoreAuditTrailConfigDao
extends GenericDao<CoreAuditTrailConfigBean, String> {
    public List<CoreAuditTrailConfigBean> selectAuditTrailableByCondition(MapperParameter var1);
}
