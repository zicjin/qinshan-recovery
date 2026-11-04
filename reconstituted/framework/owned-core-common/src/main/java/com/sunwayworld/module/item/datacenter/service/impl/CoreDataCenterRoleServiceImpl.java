/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.service.impl;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleUserDTO;
import com.sunwayworld.module.item.datacenter.dao.CoreDataCenterRoleDao;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterRoleService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDataCenterRoleServiceImpl
implements CoreDataCenterRoleService {
    @Autowired
    private CoreDataCenterRoleDao dataCenterRoleDao;

    @Override
    public CoreDataCenterRoleDao getDao() {
        return this.dataCenterRoleDao;
    }

    @Override
    public List<CoreDataCenterRoleUserDTO> selectDataCenterRoleUserListByFilter(SearchFilter filter, Order ... orders) {
        MapperParameter parameter = new MapperParameter();
        parameter.setFilter(filter);
        for (Order order : orders) {
            parameter.setOrderParam(order.getColumn(), order.getDirection());
        }
        List<Map<String, Object>> mapList = this.getDao().selectDataCenterRoleUserByCondition(parameter);
        if (mapList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        return mapList.parallelStream().map(m -> PersistableHelper.mapToPersistable(m, CoreDataCenterRoleUserDTO.class)).collect(Collectors.toList());
    }
}
