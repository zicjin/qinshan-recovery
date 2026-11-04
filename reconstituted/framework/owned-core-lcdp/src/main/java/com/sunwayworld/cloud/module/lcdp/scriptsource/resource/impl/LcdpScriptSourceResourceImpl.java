/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.scriptsource.resource.impl;

import com.sunwayworld.cloud.module.lcdp.scriptsource.bean.LcdpScriptSourceBean;
import com.sunwayworld.cloud.module.lcdp.scriptsource.resource.LcdpScriptSourceResource;
import com.sunwayworld.cloud.module.lcdp.scriptsource.service.LcdpScriptSourceService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u811a\u672c\u6e90\u7801\u8868")
@RestController
@GikamBean
public class LcdpScriptSourceResourceImpl
implements LcdpScriptSourceResource,
AbstractGenericResource<LcdpScriptSourceService, LcdpScriptSourceBean, Long> {
    @Autowired
    private LcdpScriptSourceService lcdpScriptSourceService;

    @Override
    public LcdpScriptSourceService getService() {
        return this.lcdpScriptSourceService;
    }

    @Override
    public LcdpScriptSourceBean selectByFullName(String fullName) {
        return this.getService().selectByFullName(fullName);
    }
}
