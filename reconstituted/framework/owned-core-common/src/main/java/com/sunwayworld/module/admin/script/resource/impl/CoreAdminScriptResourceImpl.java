/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.script.resource.impl;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.script.bean.CoreAdminScriptResultDTO;
import com.sunwayworld.module.admin.script.resource.CoreAdminScriptResource;
import com.sunwayworld.module.admin.script.service.CoreAdminScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminScriptResourceImpl
implements CoreAdminScriptResource {
    @Autowired
    private CoreAdminScriptService adminScriptService;

    @Override
    public CoreAdminScriptResultDTO execute(RestJsonWrapperBean wrapper) {
        return this.adminScriptService.execute(wrapper);
    }
}
