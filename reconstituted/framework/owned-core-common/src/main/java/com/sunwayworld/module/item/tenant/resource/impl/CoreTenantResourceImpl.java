/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.tenant.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.tenant.bean.CoreTenantPermissionBean;
import com.sunwayworld.module.item.tenant.resource.CoreTenantResource;
import com.sunwayworld.module.item.tenant.service.CoreTenantService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u79df\u6237")
@RestController
@GikamBean
public class CoreTenantResourceImpl
implements CoreTenantResource {
    @Autowired
    private CoreTenantService tenantService;

    @Override
    public CoreTenantService getService() {
        return this.tenantService;
    }

    @Override
    @Log(value="\u65b0\u589e\u79df\u6237", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u79df\u6237ID\u67e5\u8be2\u79df\u6237\u6743\u9650", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/permissions/queries"}, method={RequestMethod.POST})
    public Page<CoreTenantPermissionBean> selectPermissionPaginationByTenantId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectPermissionPaginationByTenantId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u79df\u6237\u6743\u9650", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/permissions"}, method={RequestMethod.POST})
    public Long insertPermission(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertPermission(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u79df\u6237\u6743\u9650", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/permissions"}, method={RequestMethod.DELETE})
    public void deletePermission(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deletePermission(id, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5f53\u524d\u767b\u5f55\u4eba\u5458\u7684\u79df\u6237", type=LogType.SELECT)
    @RequestMapping(value={"/login-tenant/queries/post-selectable"}, method={RequestMethod.POST})
    public List<CoreTenantPermissionBean> selectLoginTenantList(RestJsonWrapperBean wrapper) {
        return this.getService().selectLoginTenantList(wrapper);
    }
}
