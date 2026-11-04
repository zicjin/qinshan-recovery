/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserRoleBean;
import com.sunwayworld.module.mdm.user.dao.CoreModifiedUserRoleDao;
import com.sunwayworld.module.mdm.user.service.CoreModifiedUserRoleService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreModifiedUserRoleServiceImpl
implements CoreModifiedUserRoleService {
    @Autowired
    private CoreModifiedUserRoleDao modifiedUserRoleDao;

    @Override
    public CoreModifiedUserRoleDao getDao() {
        return this.modifiedUserRoleDao;
    }

    @Override
    @Transactional
    public void insert(Long modifiedUserId, RestJsonWrapperBean wrapper) {
        List<CoreModifiedUserRoleBean> modifiedUserRoleList = wrapper.parse(CoreModifiedUserRoleBean.class);
        modifiedUserRoleList.forEach(r -> {
            r.setId(ApplicationContextHelper.getNextIdentity());
            r.setModifiedUserId(modifiedUserId);
            r.setDefaultFlag("0");
        });
        this.getDao().insert(modifiedUserRoleList);
    }

    @Override
    @Transactional
    public void delete(Long modifiedUserId, RestJsonWrapperBean wrapper) {
        List<CoreModifiedUserRoleBean> modifiedUserRoleList = wrapper.parse(CoreModifiedUserRoleBean.class);
        this.getDao().deleteByIdList(modifiedUserRoleList.stream().map(r -> r.getId()).collect(Collectors.toList()));
    }

    @Override
    public Page<CoreModifiedUserRoleBean> selectPaginationByModifiedUserId(Long modifiedUserId, RestJsonWrapperBean wrapper) {
        return this.selectPaginationByFilter(SearchFilter.instance().match("MODIFIEDUSERID", modifiedUserId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public void setUserRoleDefault(Long modifiedUserId, Long id) {
        ArrayList<CoreModifiedUserRoleBean> updateUserRoleList = new ArrayList<CoreModifiedUserRoleBean>();
        CoreModifiedUserRoleBean filter = new CoreModifiedUserRoleBean();
        filter.setModifiedUserId(modifiedUserId);
        filter.setDefaultFlag("1");
        List<CoreModifiedUserRoleBean> defaultUserRoleList = this.getDao().selectList(filter, new Order[0]);
        if (!defaultUserRoleList.isEmpty()) {
            defaultUserRoleList.stream().filter(u -> !u.getId().equals(id)).forEach(u -> {
                u.setDefaultFlag("0");
                updateUserRoleList.add((CoreModifiedUserRoleBean)u);
            });
        }
        if (!defaultUserRoleList.stream().anyMatch(u -> u.getId().equals(id))) {
            CoreModifiedUserRoleBean defaultUserRole = new CoreModifiedUserRoleBean();
            defaultUserRole.setId(id);
            defaultUserRole.setDefaultFlag("1");
            updateUserRoleList.add(defaultUserRole);
        }
        this.getDao().update(updateUserRoleList, new String[]{"DEFAULTFLAG"});
    }
}
