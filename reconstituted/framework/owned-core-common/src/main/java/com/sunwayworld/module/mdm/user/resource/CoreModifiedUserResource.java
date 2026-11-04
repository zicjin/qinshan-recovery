/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserPermissionBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserRoleBean;
import com.sunwayworld.module.mdm.user.service.CoreModifiedUserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/mdm/m-users"})
public interface CoreModifiedUserResource
extends GenericResource<CoreModifiedUserService, CoreModifiedUserBean, Long>,
GenericActivatableResource<CoreModifiedUserService, CoreModifiedUserBean, Long> {
    @RequestMapping(value={"/source-users/{userId}"}, method={RequestMethod.POST})
    public Long insertByUserId(@PathVariable(value="userId") String var1);

    @RequestMapping(value={"/{id}/user-permissions/queries"}, method={RequestMethod.POST})
    public Page<CoreModifiedUserPermissionBean> selectUserPermissionPagination(@PathVariable(value="id") Long var1, @RequestParam(value="permissionType") String var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/user-roles/queries"}, method={RequestMethod.POST})
    public Page<CoreModifiedUserRoleBean> selectUserRolePagination(@PathVariable(value="id") Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/queries/activated"}, method={RequestMethod.POST})
    public Page<CoreModifiedUserBean> selectActivatedPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/executed"}, method={RequestMethod.POST})
    public Page<CoreModifiedUserBean> selectExecutedPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/user-roles"}, method={RequestMethod.POST})
    public void insertUserRoles(@PathVariable(value="id") Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/user-roles"}, method={RequestMethod.DELETE})
    public void deleteUserRoles(@PathVariable(value="id") Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/user-permissions"}, method={RequestMethod.POST})
    public void insertUserPermissions(@PathVariable(value="id") Long var1, @RequestParam(value="permissionType") String var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/user-permissions"}, method={RequestMethod.DELETE})
    public void deleteUserPermissions(@PathVariable(value="id") Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/user-roles/{userRoleId}/action/set-default"}, method={RequestMethod.PUT})
    public void setUserRoleDefault(@PathVariable(value="id") Long var1, @PathVariable(value="userRoleId") Long var2);

    @RequestMapping(value={"/{id}/user-permissions/{userPermissionId}/action/set-default"}, method={RequestMethod.PUT})
    public void setUserPermissionDefault(@PathVariable(value="id") Long var1, @PathVariable(value="userPermissionId") Long var2);
}
