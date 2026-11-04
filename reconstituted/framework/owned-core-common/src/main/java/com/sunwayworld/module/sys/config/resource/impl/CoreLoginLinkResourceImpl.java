/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.config.resource.CoreLoginLinkResource;
import com.sunwayworld.module.sys.config.service.CoreLoginLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9996\u9875\u767b\u5f55\u94fe\u63a5")
@RestController
@GikamBean
public class CoreLoginLinkResourceImpl
implements CoreLoginLinkResource {
    @Autowired
    private CoreLoginLinkService coreLoginLinkService;

    @Override
    public CoreLoginLinkService getService() {
        return this.coreLoginLinkService;
    }

    @Override
    @Log(value="\u65b0\u589e\u9996\u9875\u767b\u5f55\u94fe\u63a5", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
