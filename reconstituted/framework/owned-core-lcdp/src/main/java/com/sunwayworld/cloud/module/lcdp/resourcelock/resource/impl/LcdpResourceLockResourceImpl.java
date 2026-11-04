/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourcelock.resource.impl;

import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.resource.LcdpResourceLockResource;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u8d44\u6e90\u9501\u5b9a")
@RestController
@GikamBean
public class LcdpResourceLockResourceImpl
implements LcdpResourceLockResource,
AbstractGenericResource<LcdpResourceLockService, LcdpResourceLockBean, Long> {
    @Autowired
    private LcdpResourceLockService lcdpResourceLockService;

    @Override
    public LcdpResourceLockService getService() {
        return this.lcdpResourceLockService;
    }

    @Override
    @Log(value="\u6821\u9a8c\u8d44\u6e90\u662f\u5426\u53ef\u68c0\u51fa", type=LogType.SELECT)
    public RestValidationResultBean validateResourceEditable(String resourceId) {
        return this.getService().validateResourceEditable(resourceId);
    }
}
