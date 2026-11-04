/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.resource.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiNotifierBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.resource.LcdpApiNotifierResource;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiNotifierService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u63a5\u53e3\u901a\u77e5\u4eba")
@RestController
@GikamBean
public class LcdpApiNotifierResourceImpl
implements LcdpApiNotifierResource,
AbstractGenericResource<LcdpApiNotifierService, LcdpApiNotifierBean, Long> {
    @Autowired
    private LcdpApiNotifierService lcdpApiNotifierService;

    @Override
    public LcdpApiNotifierService getService() {
        return this.lcdpApiNotifierService;
    }

    @Override
    @Log(value="\u65b0\u589e\u63a5\u53e3\u901a\u77e5\u4eba", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
