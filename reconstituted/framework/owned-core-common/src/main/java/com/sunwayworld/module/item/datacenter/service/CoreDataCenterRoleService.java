/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.service;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleUserDTO;
import java.util.List;

public interface CoreDataCenterRoleService
extends GenericService<CoreDataCenterRoleBean, Long> {
    public List<CoreDataCenterRoleUserDTO> selectDataCenterRoleUserListByFilter(SearchFilter var1, Order ... var2);
}
