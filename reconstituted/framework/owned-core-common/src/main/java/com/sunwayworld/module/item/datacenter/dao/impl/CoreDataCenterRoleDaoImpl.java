/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleBean;
import com.sunwayworld.module.item.datacenter.dao.CoreDataCenterRoleDao;
import com.sunwayworld.module.item.datacenter.mapper.CoreDataCenterRoleMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDataCenterRoleDaoImpl
extends MybatisDaoSupport<CoreDataCenterRoleBean, Long>
implements CoreDataCenterRoleDao {
    @Autowired
    private CoreDataCenterRoleMapper dataCenterRoleMapper;

    public CoreDataCenterRoleMapper getMapper() {
        return this.dataCenterRoleMapper;
    }

    @Override
    public List<Map<String, Object>> selectDataCenterRoleUserByCondition(MapperParameter parameter) {
        return this.getMapper().selectDataCenterRoleUserByCondition(parameter);
    }
}
