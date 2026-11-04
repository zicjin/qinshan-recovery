/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableResource;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/roles"})
public interface CoreRoleResource
extends GenericResource<CoreRoleService, CoreRoleBean, Long>,
GenericSuspendableResource<CoreRoleService, CoreRoleBean, Long>,
GenericChoosableResource<CoreRoleService, CoreRoleBean, Long> {
    @Override
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/role-users/queries"}, method={RequestMethod.POST})
    public Page<CoreRoleUserBean> selectRoleUserPaginationByRole(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/role-users"}, method={RequestMethod.POST})
    public void insertRoleUser(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/role-users"}, method={RequestMethod.DELETE})
    public void deleteRoleUser(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/role-permission-types/{permissionTypeId}/role-permissions"}, method={RequestMethod.POST})
    public void insertRolePermission(@PathVariable Long var1, @PathVariable String var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/role-permission-types/{permissionTypeId}/role-permissions"}, method={RequestMethod.DELETE})
    public void deleteRolePermission(@PathVariable Long var1, @PathVariable String var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/role-permission-types/{permissionTypeId}/action/validate-admin"}, method={RequestMethod.GET})
    public boolean isPermissionTypeAdminRole(@PathVariable Long var1, @PathVariable String var2);

    @RequestMapping(value={"/permission-matrices/heads/queries"}, method={RequestMethod.POST})
    public List<TableHeadBean> selectPermissionMatrixHeadList();

    @RequestMapping(value={"/action/delete-validate"}, method={RequestMethod.POST})
    public RestValidationResultBean deleteValidate(RestJsonWrapperBean var1);
}
