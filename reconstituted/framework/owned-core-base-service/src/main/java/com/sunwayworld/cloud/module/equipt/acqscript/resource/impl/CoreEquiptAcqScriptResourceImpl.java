/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqscript.resource.impl;

import com.sunwayworld.cloud.module.equipt.acqscript.bean.CoreEquiptAcqScriptBean;
import com.sunwayworld.cloud.module.equipt.acqscript.resource.CoreEquiptAcqScriptResource;
import com.sunwayworld.cloud.module.equipt.acqscript.service.CoreEquiptAcqScriptService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4eea\u5668\u91c7\u96c6\u811a\u672c")
@RestController
@GikamBean
public class CoreEquiptAcqScriptResourceImpl
implements CoreEquiptAcqScriptResource,
AbstractGenericResource<CoreEquiptAcqScriptService, CoreEquiptAcqScriptBean, String>,
AbstractGenericChoosableResource<CoreEquiptAcqScriptService, CoreEquiptAcqScriptBean, String> {
    @Autowired
    private CoreEquiptAcqScriptService coreEquiptAcqScriptService;

    @Override
    public CoreEquiptAcqScriptService getService() {
        return this.coreEquiptAcqScriptService;
    }

    @Override
    @Log(value="\u65b0\u589e\u4eea\u5668\u91c7\u96c6\u811a\u672c", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        return (String)this.getService().insert(wrapper);
    }
}
