/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.dao.CoreUserPermissionDao;
import com.sunwayworld.module.mdm.user.mapper.CoreUserPermissionMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreUserPermissionDaoImpl
extends MybatisDaoSupport<CoreUserPermissionBean, Long>
implements CoreUserPermissionDao {
    @Autowired
    private CoreUserPermissionMapper userPermissionMapper;

    public CoreUserPermissionMapper getMapper() {
        return this.userPermissionMapper;
    }

    @Override
    public List<Map<String, Object>> selectOrgPermision(MapperParameter parameter) {
        return this.getMapper().selectOrgPermision(parameter);
    }

    @Override
    public List<Map<String, Object>> selectRolePermision(MapperParameter parameter) {
        return this.getMapper().selectRolePermision(parameter);
    }
}
