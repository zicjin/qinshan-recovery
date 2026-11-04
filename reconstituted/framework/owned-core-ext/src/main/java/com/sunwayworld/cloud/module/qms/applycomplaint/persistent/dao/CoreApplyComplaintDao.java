/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.applycomplaint.persistent.dao;

import com.sunwayworld.cloud.module.qms.applycomplaint.bean.CoreApplyComplaintBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;
import java.util.Map;

public interface CoreApplyComplaintDao
extends GenericDao<CoreApplyComplaintBean, Long> {
    public List<Map<String, Object>> selectAuditStatusByCondition(MapperParameter var1);
}
