/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserPermissionBean;
import com.sunwayworld.module.mdm.user.dao.CoreModifiedUserPermissionDao;
import com.sunwayworld.module.mdm.user.service.CoreModifiedUserPermissionService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreModifiedUserPermissionServiceImpl
implements CoreModifiedUserPermissionService {
    @Autowired
    private CoreModifiedUserPermissionDao modifiedUserPermissionDao;

    @Override
    public CoreModifiedUserPermissionDao getDao() {
        return this.modifiedUserPermissionDao;
    }

    @Override
    @Transactional
    public void insert(Long modifiedUserId, String permissionTypeId, RestJsonWrapperBean wrapper) {
        List<CoreModifiedUserPermissionBean> modifiedUserPermissionList = wrapper.parse(CoreModifiedUserPermissionBean.class);
        modifiedUserPermissionList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setModifiedUserId(modifiedUserId);
            e.setPermissionTypeId(permissionTypeId);
        });
        this.getDao().insert(modifiedUserPermissionList);
    }

    @Override
    @Transactional
    public void delete(Long modifiedUserId, RestJsonWrapperBean wrapper) {
        this.delete(wrapper);
    }

    @Override
    public Page<CoreModifiedUserPermissionBean> selectPaginationByPermissionType(Long modifiedUserId, String permissionTypeId, RestJsonWrapperBean wrapper) {
        SearchFilter searchFilter = SearchFilter.instance().match("MODIFIEDUSERID", modifiedUserId).filter(MatchPattern.EQ).match("PERMISSIONTYPEID", permissionTypeId).filter(MatchPattern.EQ);
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(searchFilter);
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return this.selectPagination(() -> this.selectPermissionList(permissionTypeId, parameter), rowBounds);
    }

    @Override
    @Transactional
    public void setUserPermissionDefault(Long modifiedUserId, Long id) {
        CoreModifiedUserPermissionBean userPermission = (CoreModifiedUserPermissionBean)this.getDao().selectById(id);
        ArrayList<CoreModifiedUserPermissionBean> updateUserPermissionList = new ArrayList<CoreModifiedUserPermissionBean>();
        CoreModifiedUserPermissionBean filter = new CoreModifiedUserPermissionBean();
        filter.setModifiedUserId(modifiedUserId);
        filter.setPermissionTypeId(userPermission.getPermissionTypeId());
        filter.setDefaultFlag("1");
        List<CoreModifiedUserPermissionBean> defaultUserPermissionList = this.getDao().selectList(filter, new Order[0]);
        if (!defaultUserPermissionList.isEmpty()) {
            defaultUserPermissionList.stream().filter(u -> !u.getId().equals(id)).forEach(u -> {
                u.setDefaultFlag("0");
                updateUserPermissionList.add((CoreModifiedUserPermissionBean)u);
            });
        }
        if (!defaultUserPermissionList.stream().anyMatch(u -> u.getId().equals(id))) {
            userPermission.setDefaultFlag("1");
            updateUserPermissionList.add(userPermission);
        }
        this.getDao().update(updateUserPermissionList, new String[]{"DEFAULTFLAG"});
    }

    @Override
    public List<CoreModifiedUserPermissionBean> selectPermissionList(String permissionTypeId, MapperParameter parameter) {
        if ("T_CORE_ORG".equals(permissionTypeId)) {
            return this.getDao().selectOrgPermissionList(parameter);
        }
        return this.getDao().selectByCondition(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreModifiedUserPermissionBean.class)).collect(Collectors.toList());
    }
}
