/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.resource;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantDTO;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/open/core/module/admin/tenants"})
public interface CoreAdminTenantOpenResource {
    @Log(value="\u83b7\u53d6\u8981\u767b\u5f55\u7528\u6237\u6743\u9650\u8303\u56f4\u5185\u7684\u79df\u6237", type=LogType.INSERT)
    @RequestMapping(value={"/pre-login/queries/post-selectable"}, method={RequestMethod.POST})
    public List<CoreAdminTenantDTO> selectLoginTenantList(RestJsonWrapperBean var1);
}
