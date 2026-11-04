/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.resource.CoreStudioAdvSearchConfigResource;
import com.sunwayworld.module.design.studio.service.CoreStudioAdvSearchConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreStudioAdvSearchConfigResourceImpl
implements CoreStudioAdvSearchConfigResource {
    @Autowired
    private CoreStudioAdvSearchConfigService configService;

    @Override
    public CoreStudioAdvSearchConfigService getService() {
        return this.configService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6216\u66f4\u65b0\u9ad8\u7ea7\u67e5\u8be2\u7684\u914d\u7f6e", type=LogType.UPDATE)
    public Long save(RestJsonWrapperBean wrapper) {
        return this.getService().save(wrapper);
    }
}
