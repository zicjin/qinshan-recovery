/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.ncr.resource.impl;

import com.sunwayworld.cloud.module.qms.ncr.bean.CoreNcrBean;
import com.sunwayworld.cloud.module.qms.ncr.resource.CoreNcrResource;
import com.sunwayworld.cloud.module.qms.ncr.service.CoreNcrService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6570\u636e\u8868 \u4e0d\u7b26\u5408\u767b\u8bb0")
@RestController
@GikamBean
public class CoreNcrResourceImpl
implements CoreNcrResource,
AbstractGenericResource<CoreNcrService, CoreNcrBean, Long>,
GenericAuditableResource<CoreNcrService, CoreNcrBean, Long>,
GenericChoosableResource<CoreNcrService, CoreNcrBean, Long> {
    @Autowired
    private CoreNcrService coreNcrService;

    @Override
    public CoreNcrService getService() {
        return this.coreNcrService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6570\u636e\u8868 \u4e0d\u7b26\u5408\u767b\u8bb0", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
