/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleBean;
import java.util.List;
import java.util.Map;

public interface CoreDataCenterRoleDao
extends GenericDao<CoreDataCenterRoleBean, Long> {
    public List<Map<String, Object>> selectDataCenterRoleUserByCondition(MapperParameter var1);
}
