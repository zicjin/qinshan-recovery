/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.resource.CoreRoleResource;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u89d2\u8272")
@RestController
@GikamBean
public class CoreRoleResourceImpl
implements CoreRoleResource {
    @Autowired
    private CoreRoleService roleService;

    @Override
    public CoreRoleService getService() {
        return this.roleService;
    }

    @Override
    @Log(value="\u65b0\u589e\u89d2\u8272", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u89d2\u8272\u6765\u67e5\u8be2\u6240\u5c5e\u7528\u6237", type=LogType.SELECT)
    public Page<CoreRoleUserBean> selectRoleUserPaginationByRole(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectRoleUserPaginationByRole(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u89d2\u8272\u7528\u6237", type=LogType.INSERT)
    public void insertRoleUser(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().insertRoleUser(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u89d2\u8272\u7528\u6237", type=LogType.DELETE)
    public void deleteRoleUser(RestJsonWrapperBean wrapper) {
        this.getService().deleteRoleUser(wrapper);
    }

    @Override
    @Log(value="\u6307\u5b9a\u89d2\u8272\u4e0b\u65b0\u589e\u6307\u5b9a\u7c7b\u578b\u7684\u6743\u9650", type=LogType.INSERT)
    public void insertRolePermission(@PathVariable Long id, @PathVariable String permissionTypeId, RestJsonWrapperBean wrapper) {
        this.getService().insertRolePermission(id, permissionTypeId, wrapper);
    }

    @Override
    @Log(value="\u6307\u5b9a\u89d2\u8272\u4e0b\u5220\u9664\u6307\u5b9a\u7c7b\u578b\u4e0b\u7684\u6743\u9650", type=LogType.INSERT)
    public void deleteRolePermission(@PathVariable Long id, @PathVariable String permissionTypeId, RestJsonWrapperBean wrapper) {
        this.getService().deleteRolePermission(id, permissionTypeId, wrapper);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u89d2\u8272\u662f\u5426\u662f\u6307\u5b9a\u7c7b\u578b\u6743\u9650\u7684\u7ba1\u7406\u5458", type=LogType.INSERT)
    public boolean isPermissionTypeAdminRole(@PathVariable Long id, @PathVariable String permissionTypeId) {
        return this.getService().isPermissionTypeAdminRole(id, permissionTypeId);
    }

    @Override
    @Log(value="\u67e5\u8be2\u89d2\u8272\u7528\u6237\u6743\u9650\u6807\u9898", type=LogType.SELECT)
    public List<TableHeadBean> selectPermissionMatrixHeadList() {
        return this.getService().selectPermissionMatrixHeadList();
    }

    @Override
    @Log(value="\u89d2\u8272\u5220\u9664\u6821\u9a8c", type=LogType.VALIDATE)
    public RestValidationResultBean deleteValidate(RestJsonWrapperBean wrapper) {
        return this.getService().deleteValidate(wrapper);
    }
}
