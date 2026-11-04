/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.request.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.request.resource.CoreRequestUrlResource;
import com.sunwayworld.module.admin.request.service.CoreRequestUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8def\u5f84\u914d\u7f6e")
@RestController
@GikamBean
public class CoreRequestUrlResourceImpl
implements CoreRequestUrlResource {
    @Autowired
    private CoreRequestUrlService coreRequestUrlService;

    @Override
    public CoreRequestUrlService getService() {
        return this.coreRequestUrlService;
    }

    @Override
    @Log(value="\u65b0\u589e\u8def\u5f84\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
