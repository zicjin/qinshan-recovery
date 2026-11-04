/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.resource.impl;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantDTO;
import com.sunwayworld.module.admin.tenant.resource.CoreAdminTenantOpenResource;
import com.sunwayworld.module.admin.tenant.service.CoreAdminTenantService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminTenantOpenResourceImpl
implements CoreAdminTenantOpenResource {
    @Autowired
    private CoreAdminTenantService tenantService;

    @Override
    public List<CoreAdminTenantDTO> selectLoginTenantList(RestJsonWrapperBean wrapper) {
        return this.tenantService.selectLoginTenantList(wrapper);
    }
}
