/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service.impl;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuExtBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuExtDao;
import com.sunwayworld.module.sys.menu.service.CoreMenuExtService;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuExtServiceImpl
implements CoreMenuExtService {
    @Autowired
    private CoreMenuExtDao menuExtDao;
    @Autowired
    @Lazy
    private CoreRolePermissionService permissionService;
    @Autowired
    @Lazy
    private CoreMenuExtService proxyInstance;

    @Override
    public CoreMenuExtDao getDao() {
        return this.menuExtDao;
    }

    @Override
    @Cacheable(value={"T_CORE_MENU_EXT"}, key="'ALL'")
    public List<CoreMenuExtBean> selectAll() {
        return CoreMenuExtService.super.selectAll();
    }

    @Override
    public List<CoreMenuExtBean> selectCurrentUserMenuExtList() {
        if (!LocalContextHelper.isUserLogin()) {
            return CollectionUtils.emptyList();
        }
        List<CoreRolePermissionBean> permissionList = this.permissionService.selectActiveUserPermissionList(LocalContextHelper.getLoginUserId(), "T_CORE_MENU");
        if (LocalContextHelper.getLoginRoleId() != null) {
            permissionList.removeIf(p -> !p.getRoleId().equals(LocalContextHelper.getLoginRoleId()));
        }
        if (permissionList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        boolean admin = permissionList.stream().anyMatch(p -> "0".equals(p.getTargetId()));
        if (admin) {
            return this.proxyInstance.selectAll();
        }
        List permittedMenuIdList = permissionList.stream().map(p -> p.getTargetId()).distinct().collect(Collectors.toList());
        if (permittedMenuIdList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        return this.getDao().selectListByIds(permittedMenuIdList, new Order[0]);
    }

    @Override
    public Page<CoreMenuExtBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
        return CoreMenuExtService.super.selectChoosablePagination(wrapper);
    }
}
