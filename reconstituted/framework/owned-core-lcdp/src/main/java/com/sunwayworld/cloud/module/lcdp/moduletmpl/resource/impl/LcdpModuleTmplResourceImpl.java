/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.resource.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.resource.LcdpModuleTmplResource;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpModuleTmplService;
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

@LogModule(value="\u6a21\u5757\u6a21\u677f")
@RestController
@GikamBean
public class LcdpModuleTmplResourceImpl
implements LcdpModuleTmplResource,
AbstractGenericResource<LcdpModuleTmplService, LcdpModuleTmplBean, Long> {
    @Autowired
    private LcdpModuleTmplService lcdpModuleTmplService;

    @Override
    public LcdpModuleTmplService getService() {
        return this.lcdpModuleTmplService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6a21\u5757\u6a21\u677f", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    public void saveCustomTemplate(RestJsonWrapperBean wrapper) {
        this.getService().saveCustomTemplate(wrapper);
    }
}
