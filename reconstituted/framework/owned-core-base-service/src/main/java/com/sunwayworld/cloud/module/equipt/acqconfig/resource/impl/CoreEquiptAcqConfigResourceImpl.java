/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqconfig.resource.impl;

import com.sunwayworld.cloud.module.equipt.acqconfig.bean.CoreEquiptAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.acqconfig.resource.CoreEquiptAcqConfigResource;
import com.sunwayworld.cloud.module.equipt.acqconfig.service.CoreEquiptAcqConfigService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4eea\u5668\u91c7\u96c6\u914d\u7f6e")
@RestController
@GikamBean
public class CoreEquiptAcqConfigResourceImpl
implements CoreEquiptAcqConfigResource,
AbstractGenericResource<CoreEquiptAcqConfigService, CoreEquiptAcqConfigBean, String>,
AbstractGenericChoosableResource<CoreEquiptAcqConfigService, CoreEquiptAcqConfigBean, String> {
    @Autowired
    private CoreEquiptAcqConfigService coreEquiptAcqConfigService;

    @Override
    public CoreEquiptAcqConfigService getService() {
        return this.coreEquiptAcqConfigService;
    }

    @Override
    @Log(value="\u8fde\u63a5\u4eea\u5668", type=LogType.UPDATE)
    public String connect(String id, RestJsonWrapperBean wrapper) {
        return this.getService().connect(id, wrapper);
    }
}
