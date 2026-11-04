/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.matrix.resource.GenericMatrixResource;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/core/module/sys/role-users"})
public interface CoreRoleUserResource
extends GenericResource<CoreRoleUserService, CoreRoleUserBean, Long>,
GenericMatrixResource {
    @RequestMapping(value={"/users/{userId}/action/set-default"}, method={RequestMethod.PUT})
    public void setDefault(@PathVariable String var1, RestJsonWrapperBean var2);
}
