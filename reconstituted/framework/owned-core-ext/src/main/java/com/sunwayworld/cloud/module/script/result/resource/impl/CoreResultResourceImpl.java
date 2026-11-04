/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.result.resource.impl;

import com.sunwayworld.cloud.module.script.result.bean.CoreResultBean;
import com.sunwayworld.cloud.module.script.result.resource.CoreResultResource;
import com.sunwayworld.cloud.module.script.result.service.CoreResultService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6d4b\u8bd5")
@RestController
@GikamBean
public class CoreResultResourceImpl
implements CoreResultResource,
AbstractGenericResource<CoreResultService, CoreResultBean, Long> {
    @Autowired
    private CoreResultService coreResultService;

    @Override
    public CoreResultService getService() {
        return this.coreResultService;
    }

    @Override
    @Log(value="\u516c\u5f0f\u8ba1\u7b97", type=LogType.UPDATE)
    public void compute(RestJsonWrapperBean wrapper) {
        this.getService().compute(wrapper);
    }
}
