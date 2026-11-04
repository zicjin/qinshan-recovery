/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserPermissionBean;
import java.util.List;

public interface CoreModifiedUserPermissionDao
extends GenericDao<CoreModifiedUserPermissionBean, Long> {
    public List<CoreModifiedUserPermissionBean> selectOrgPermissionList(MapperParameter var1);
}
