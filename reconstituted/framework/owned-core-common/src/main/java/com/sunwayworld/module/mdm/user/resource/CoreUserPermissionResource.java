/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/mdm/user-permissions"})
public interface CoreUserPermissionResource
extends GenericResource<CoreUserPermissionService, CoreUserPermissionBean, Long> {
    public Page<CoreUserPermissionBean> selectPaginationByPermissionType(String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/login-role/queries/post-selectable"}, method={RequestMethod.POST})
    public List<CoreUserPermissionBean> selectLoginRoleList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/login-org/queries/post-selectable"}, method={RequestMethod.POST})
    public List<CoreUserPermissionBean> selectLoginOrgList(RestJsonWrapperBean var1);
}
