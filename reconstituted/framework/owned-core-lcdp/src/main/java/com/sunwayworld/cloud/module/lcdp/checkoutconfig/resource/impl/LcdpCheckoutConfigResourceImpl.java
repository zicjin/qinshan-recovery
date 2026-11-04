/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutconfig.resource.impl;

import com.sunwayworld.cloud.module.lcdp.checkoutconfig.bean.LcdpCheckoutConfigBean;
import com.sunwayworld.cloud.module.lcdp.checkoutconfig.resource.LcdpCheckoutConfigResource;
import com.sunwayworld.cloud.module.lcdp.checkoutconfig.service.LcdpCheckoutConfigService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8fc1\u51fa\u914d\u7f6e\u8868")
@RestController
@GikamBean
public class LcdpCheckoutConfigResourceImpl
implements LcdpCheckoutConfigResource,
AbstractGenericResource<LcdpCheckoutConfigService, LcdpCheckoutConfigBean, Long>,
AbstractGenericActivatableResource<LcdpCheckoutConfigService, LcdpCheckoutConfigBean, Long> {
    @Autowired
    private LcdpCheckoutConfigService lcdpCheckoutConfigService;

    @Override
    public LcdpCheckoutConfigService getService() {
        return this.lcdpCheckoutConfigService;
    }

    @Override
    @Log(value="\u65b0\u589e\u8fc1\u51fa\u914d\u7f6e\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
