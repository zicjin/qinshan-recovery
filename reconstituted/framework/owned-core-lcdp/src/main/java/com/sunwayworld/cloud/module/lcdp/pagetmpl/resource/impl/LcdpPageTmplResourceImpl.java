/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.pagetmpl.resource.impl;

import com.sunwayworld.cloud.module.lcdp.pagetmpl.bean.LcdpPageTmplBean;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.resource.LcdpPageTmplResource;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.service.LcdpPageTmplService;
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

@LogModule(value="\u9875\u9762\u6a21\u677f\u8868")
@RestController
@GikamBean
public class LcdpPageTmplResourceImpl
implements LcdpPageTmplResource,
AbstractGenericResource<LcdpPageTmplService, LcdpPageTmplBean, Long> {
    @Autowired
    private LcdpPageTmplService lcdpPageTmplService;

    @Override
    public LcdpPageTmplService getService() {
        return this.lcdpPageTmplService;
    }

    @Override
    @Log(value="\u65b0\u589e\u9875\u9762\u6a21\u677f\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
