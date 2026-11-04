/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.code.resource.CoreCodeResource;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u7f16\u7801")
@RestController
@GikamBean
public class CoreCodeResourceImpl
implements CoreCodeResource {
    @Autowired
    private CoreCodeService codeService;

    @Override
    public CoreCodeService getService() {
        return this.codeService;
    }

    @Override
    @Log(value="\u65b0\u589e", type=LogType.INSERT)
    @ResponseStatus(value=HttpStatus.OK)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        return this.getService().insert(jsonWrapper);
    }
}
