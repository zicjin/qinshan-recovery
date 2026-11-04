/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.submitlogtmpl.resource.impl;

import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.bean.LcdpSubmitLogTmplBean;
import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.resource.LcdpSubmitLogTmplResource;
import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.service.LcdpSubmitLogTmplService;
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

@LogModule(value="\u63d0\u4ea4\u65e5\u5fd7\u6a21\u677f\u8868")
@RestController
@GikamBean
public class LcdpSubmitLogTmplResourceImpl
implements LcdpSubmitLogTmplResource,
AbstractGenericResource<LcdpSubmitLogTmplService, LcdpSubmitLogTmplBean, Long>,
AbstractGenericActivatableResource<LcdpSubmitLogTmplService, LcdpSubmitLogTmplBean, Long> {
    @Autowired
    private LcdpSubmitLogTmplService lcdpSubmitLogTmplService;

    @Override
    public LcdpSubmitLogTmplService getService() {
        return this.lcdpSubmitLogTmplService;
    }

    @Override
    @Log(value="\u65b0\u589e\u63d0\u4ea4\u65e5\u5fd7\u6a21\u677f\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
