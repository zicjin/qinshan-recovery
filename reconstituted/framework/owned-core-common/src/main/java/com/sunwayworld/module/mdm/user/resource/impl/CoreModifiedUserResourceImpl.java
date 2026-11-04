/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserPermissionBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserRoleBean;
import com.sunwayworld.module.mdm.user.resource.CoreModifiedUserResource;
import com.sunwayworld.module.mdm.user.service.CoreModifiedUserService;
import com.sunwayworld.module.mdm.user.validator.CoreModifiedUserActivateDataValidator;
import com.sunwayworld.module.mdm.user.validator.CoreModifiedUserDeactivateDataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9884\u7ea6\u53d8\u66f4\u7684\u7528\u6237")
@RestController
@GikamBean
public class CoreModifiedUserResourceImpl
implements CoreModifiedUserResource {
    @Autowired
    private CoreModifiedUserService modifiedUserService;

    @Override
    public CoreModifiedUserService getService() {
        return this.modifiedUserService;
    }

    @Override
    @Log(value="\u901a\u8fc7\u7528\u6237ID\u751f\u6210\u9884\u7ea6\u53d8\u66f4\u7684\u7528\u6237\u4fe1\u606f", type=LogType.SELECT)
    public Long insertByUserId(String userId) {
        return this.getService().insertByUserId(userId);
    }

    @Override
    @Log(value="\u901a\u8fc7\u53d8\u66f4\u7684\u7528\u6237ID\u548c\u89d2\u8272\u7f16\u7801\u6765\u5206\u9875\u67e5\u8be2\u6743\u9650", type=LogType.SELECT)
    public Page<CoreModifiedUserPermissionBean> selectUserPermissionPagination(@PathVariable(value="id") Long id, String permissionType, RestJsonWrapperBean wrapper) {
        return this.getService().selectUserPermissionPagination(id, permissionType, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u53d8\u66f4\u7684\u7528\u6237ID\u6765\u5206\u9875\u67e5\u8be2\u89d2\u8272", type=LogType.SELECT)
    public Page<CoreModifiedUserRoleBean> selectUserRolePagination(@PathVariable(value="id") Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectUserRolePagination(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5df2\u542f\u7528\u672a\u6267\u884c\u53d8\u66f4\u7528\u6237", type=LogType.SELECT)
    public Page<CoreModifiedUserBean> selectActivatedPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectActivatedPagination(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5df2\u6267\u884c\u7684\u53d8\u66f4\u7528\u6237", type=LogType.SELECT)
    public Page<CoreModifiedUserBean> selectExecutedPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectExecutedPagination(wrapper);
    }

    @Override
    @Log(value="\u6fc0\u6d3b", type=LogType.ACTIVATE)
    @ValidateDataWith(value=CoreModifiedUserActivateDataValidator.class)
    public void activate(RestJsonWrapperBean wrapper) {
        this.getService().activate(wrapper);
    }

    @Override
    @Log(value="\u5931\u6548", type=LogType.ACTIVATE)
    @ValidateDataWith(value=CoreModifiedUserDeactivateDataValidator.class)
    public void deactivate(RestJsonWrapperBean wrapper) {
        this.getService().deactivate(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u53d8\u66f4\u7528\u6237\u7684\u89d2\u8272", type=LogType.INSERT)
    public void insertUserRoles(@PathVariable(value="id") Long id, RestJsonWrapperBean wrapper) {
        this.getService().insertUserRoles(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u53d8\u66f4\u7528\u6237\u7684\u89d2\u8272", type=LogType.DELETE)
    public void deleteUserRoles(@PathVariable(value="id") Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteUserRoles(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u53d8\u66f4\u7528\u6237\u7684\u6743\u9650", type=LogType.INSERT)
    public void insertUserPermissions(@PathVariable(value="id") Long id, @RequestParam(value="permissionType") String permissionType, RestJsonWrapperBean wrapper) {
        this.getService().insertUserPermission(id, permissionType, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u53d8\u66f4\u7528\u6237\u7684\u6743\u9650", type=LogType.DELETE)
    public void deleteUserPermissions(@PathVariable(value="id") Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteUserPermission(id, wrapper);
    }

    @Override
    @Log(value="\u8bbe\u7f6e\u53d8\u66f4\u7528\u6237\u7684\u9ed8\u8ba4\u89d2\u8272", type=LogType.INSERT)
    public void setUserRoleDefault(Long id, Long userRoleId) {
        this.getService().setUserRoleDefault(id, userRoleId);
    }

    @Override
    @Log(value="\u8bbe\u7f6e\u53d8\u66f4\u7528\u6237\u7684\u9ed8\u8ba4\u6743\u9650", type=LogType.INSERT)
    public void setUserPermissionDefault(Long id, Long userPermissionId) {
        this.getService().setUserPermissionDefault(id, userPermissionId);
    }
}
