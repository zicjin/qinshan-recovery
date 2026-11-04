/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.service.impl;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionTypeBean;
import com.sunwayworld.module.sys.role.dao.CoreRolePermissionTypeDao;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreRolePermissionTypeServiceImpl
implements CoreRolePermissionTypeService {
    @Autowired
    private CoreRolePermissionTypeDao permissionTypeDao;

    @Override
    public CoreRolePermissionTypeDao getDao() {
        return this.permissionTypeDao;
    }

    @Override
    public List<CoreRolePermissionTypeBean> selectListByPermissionType(String permissionType) {
        CoreRolePermissionTypeBean condition = new CoreRolePermissionTypeBean();
        condition.setPermissionType(permissionType);
        return this.getDao().selectList(condition, new Order[0]);
    }
}
