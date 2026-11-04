/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.resource.impl;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpConfigCompareDTO;
import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalConfigBean;
import com.sunwayworld.cloud.module.lcdp.config.resource.LcdpGlobalConfigResource;
import com.sunwayworld.cloud.module.lcdp.config.service.LcdpGlobalConfigService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4f4e\u4ee3\u7801\u7cfb\u7edf\u5168\u5c40\u914d\u7f6e")
@RestController
@GikamBean
public class LcdpGlobalConfigResourceImpl
implements LcdpGlobalConfigResource,
AbstractGenericResource<LcdpGlobalConfigService, LcdpGlobalConfigBean, Long> {
    @Autowired
    private LcdpGlobalConfigService lcdpGlobalConfigService;

    @Override
    public LcdpGlobalConfigService getService() {
        return this.lcdpGlobalConfigService;
    }

    @Override
    @Log(value="\u6839\u636e\u914d\u7f6e\u7f16\u7801\u67e5\u8be2\u6fc0\u6d3b\u7684\u914d\u7f6e\u4fe1\u606f", type=LogType.SELECT)
    public LcdpGlobalConfigBean selectConfigContent(String configCode) {
        return this.getService().selectConfigContent(configCode);
    }

    @Override
    @Log(value="\u63d0\u4ea4\u5e76\u6fc0\u6d3b\u914d\u7f6e", type=LogType.INSERT)
    public void submit(RestJsonWrapperBean wrapper) {
        this.getService().submit(wrapper);
    }

    @Override
    @Log(value="\u6fc0\u6d3b\u914d\u7f6e", type=LogType.ACTIVATE)
    public void activate(RestJsonWrapperBean wrapper) {
        this.getService().activate(wrapper);
    }

    @Override
    @Log(value="\u5bf9\u6bd4\u914d\u7f6e", type=LogType.SELECT)
    public LcdpConfigCompareDTO compare(RestJsonWrapperBean wrapper) {
        return this.getService().compare(wrapper);
    }
}
