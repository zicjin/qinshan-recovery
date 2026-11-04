/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.resource;

import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableResource;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserSignatureDTO;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value={"/secure/core/module/mdm/users"})
public interface CoreUserResource
extends GenericResource<CoreUserService, CoreUserBean, String>,
GenericSuspendableResource<CoreUserService, CoreUserBean, String>,
GenericChoosableResource<CoreUserService, CoreUserBean, String>,
GenericSelectableResource<CoreUserService, CoreUserBean, String> {
    @RequestMapping(value={"/current-user/action/change-password"}, method={RequestMethod.PUT})
    public RestHttpTextEntity changePassword(RestJsonWrapperBean var1);

    @RequestMapping(value={"/current-user/action/change-new-password"}, method={RequestMethod.PUT})
    public RestHttpTextEntity changeNewPassword(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/action/change-password"}, method={RequestMethod.PUT})
    public RestHttpTextEntity changePassword(String var1, RestJsonWrapperBean var2);

    @Override
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean var1);

    @ResponseStatus(value=HttpStatus.OK)
    @RequestMapping(value={"/action/password-reset"}, method={RequestMethod.PUT})
    public void resetPassword(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/deprecate"}, method={RequestMethod.PUT})
    public void deprecate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/selected"}, method={RequestMethod.POST})
    public void insertUserPermission(String var1, String var2, RestJsonWrapperBean var3);

    public void deleteUserPermission(String var1, String var2, RestJsonWrapperBean var3);

    public void setDefaultUserPermission(String var1, String var2, RestJsonWrapperBean var3);

    public CoreUserSignatureDTO sign(RestJsonWrapperBean var1);

    public CoreUserBean selectDetailInfo(String var1);
}
