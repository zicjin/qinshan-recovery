/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.resource.impl;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserSignatureDTO;
import com.sunwayworld.module.mdm.user.resource.CoreUserResource;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u7528\u6237")
@RestController
@GikamBean
public class CoreUserResourceImpl
implements CoreUserResource {
    @Autowired
    private CoreUserService userService;

    @Override
    public CoreUserService getService() {
        return this.userService;
    }

    @Override
    @Log(value="\u65b0\u589e\u7528\u6237", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u7528\u6237\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/{id}", "/online/current-user"}, method={RequestMethod.GET})
    public CoreUserBean selectDetail(@PathVariable(required=false) String id) {
        return (CoreUserBean)CoreUserResource.super.selectDetail(Objects.toString(id, LocalContextHelper.getLoginUserId()));
    }

    @Override
    @Log(value="\u67e5\u8be2\u7528\u6237\u4fe1\u606f", type=LogType.SELECT)
    @ResponseBody
    @RequestMapping(value={"/{id}/info"}, method={RequestMethod.GET})
    public CoreUserBean selectDetailInfo(@PathVariable String id) {
        return (CoreUserBean)CoreUserResource.super.selectDetail(id);
    }

    @Override
    @Log(value="\u4fee\u6539\u767b\u5f55\u7528\u6237\u5bc6\u7801", type=LogType.UPDATE)
    public RestHttpTextEntity changePassword(RestJsonWrapperBean wrapper) {
        return this.getService().changePassword(wrapper);
    }

    @Override
    public RestHttpTextEntity changeNewPassword(RestJsonWrapperBean wrapper) {
        return this.getService().changeNewPassword(wrapper);
    }

    @Override
    @Log(value="\u4fee\u6539\u7528\u6237\u5bc6\u7801", type=LogType.UPDATE)
    public RestHttpTextEntity changePassword(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().changePassword(id, wrapper);
    }

    @Override
    @Log(value="\u91cd\u7f6e\u7cfb\u7edf\u7528\u6237\u7684\u767b\u5f55\u5bc6\u7801", type=LogType.UPDATE)
    public void resetPassword(RestJsonWrapperBean wrapper) {
        this.userService.resetPassword(wrapper);
    }

    @Override
    @Log(value="\u5e9f\u5f03", type=LogType.UPDATE)
    public void deprecate(RestJsonWrapperBean wrapper) {
        this.getService().deprecate(wrapper);
    }

    @Override
    @Log(value="\u6307\u5b9a\u7528\u6237\u4e0b\u65b0\u589e\u6307\u5b9a\u7c7b\u578b\u7684\u6743\u9650", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/user-permission-types/{permissionTypeId}/user-permissions"}, method={RequestMethod.POST})
    public void insertUserPermission(@PathVariable String id, @PathVariable String permissionTypeId, RestJsonWrapperBean wrapper) {
        this.getService().insertUserPermission(id, permissionTypeId, wrapper);
    }

    @Override
    @Log(value="\u6307\u5b9a\u7528\u6237\u4e0b\u5220\u9664\u6307\u5b9a\u7c7b\u578b\u4e0b\u7684\u6743\u9650", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/user-permission-types/{permissionTypeId}/user-permissions"}, method={RequestMethod.DELETE})
    public void deleteUserPermission(@PathVariable String id, @PathVariable String permissionTypeId, RestJsonWrapperBean wrapper) {
        this.getService().deleteUserPermission(id, permissionTypeId, wrapper);
    }

    @Override
    @Log(value="\u8bbe\u7f6e\u9ed8\u8ba4\u6307\u5b9a\u7528\u6237\u4e0b\u6307\u5b9a\u7c7b\u578b\u4e0b\u7684\u6743\u9650", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}/user-permission-types/{permissionTypeId}/user-permissions/action/set-default"}, method={RequestMethod.PUT})
    public void setDefaultUserPermission(@PathVariable String id, @PathVariable String permissionTypeId, RestJsonWrapperBean wrapper) {
        this.getService().setDefaultUserPermission(id, permissionTypeId, wrapper);
    }

    @Override
    @Log(value="\u7535\u5b50\u7b7e\u540d", type=LogType.VALIDATE)
    @RequestMapping(value={"/action/sign"}, method={RequestMethod.POST})
    public CoreUserSignatureDTO sign(RestJsonWrapperBean wrapper) {
        return this.getService().sign(wrapper);
    }
}
