/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantBean;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantUserBean;
import com.sunwayworld.module.admin.tenant.service.CoreAdminTenantService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/admin/tenants"})
public interface CoreAdminTenantResource
extends GenericResource<CoreAdminTenantService, CoreAdminTenantBean, String> {
    @Log(value="\u79df\u6237\u65b0\u589e\u7528\u6237", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/users"}, method={RequestMethod.POST})
    public void insertUsers(@PathVariable String var1, RestJsonWrapperBean var2);

    @Log(value="\u79df\u6237\u5220\u9664\u7528\u6237", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/users"}, method={RequestMethod.DELETE})
    public void deleteUsers(@PathVariable String var1, RestJsonWrapperBean var2);

    @Log(value="\u79df\u6237\u5206\u914d\u7528\u6237\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/users/queries"}, method={RequestMethod.POST})
    public Page<CoreAdminTenantUserBean> selectUserPagination(@PathVariable String var1, RestJsonWrapperBean var2);
}
