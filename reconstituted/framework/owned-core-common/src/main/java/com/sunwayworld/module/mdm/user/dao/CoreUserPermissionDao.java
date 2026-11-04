/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import java.util.List;
import java.util.Map;

public interface CoreUserPermissionDao
extends GenericDao<CoreUserPermissionBean, Long> {
    public List<Map<String, Object>> selectOrgPermision(MapperParameter var1);

    public List<Map<String, Object>> selectRolePermision(MapperParameter var1);
}
