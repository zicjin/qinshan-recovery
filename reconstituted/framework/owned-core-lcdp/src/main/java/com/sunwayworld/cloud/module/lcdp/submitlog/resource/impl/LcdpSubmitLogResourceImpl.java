/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.submitlog.resource.impl;

import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.resource.LcdpSubmitLogResource;
import com.sunwayworld.cloud.module.lcdp.submitlog.service.LcdpSubmitLogService;
import com.sunwayworld.framework.data.page.Page;
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

@LogModule(value="\u63d0\u4ea4\u65e5\u5fd7")
@RestController
@GikamBean
public class LcdpSubmitLogResourceImpl
implements LcdpSubmitLogResource,
AbstractGenericResource<LcdpSubmitLogService, LcdpSubmitLogBean, Long> {
    @Autowired
    private LcdpSubmitLogService lcdpSubmitLogService;

    @Override
    public LcdpSubmitLogService getService() {
        return this.lcdpSubmitLogService;
    }

    @Override
    @Log(value="\u65b0\u589e\u63d0\u4ea4\u65e5\u5fd7", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u63d0\u4ea4", type=LogType.INSERT)
    public void submitResource(RestJsonWrapperBean wrapper) {
        this.getService().submitResource(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u63d0\u4ea4\u65e5\u5fd7\u8868ID\u67e5\u8be2\u63d0\u4ea4\u4fe1\u606f", type=LogType.SELECT)
    public Page<LcdpResourceVersionBean> selectVersionPaginationByLogId(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectVersionPaginationByLogId(id, wrapper);
    }

    @Override
    @Log(value="\u9009\u62e9\u65e5\u5fd7\u4fe1\u606f\u67e5\u770b\u63d0\u4ea4\u7684\u8d44\u6e90", type=LogType.SELECT)
    public Page<LcdpResourceVersionBean> viewResource(RestJsonWrapperBean wrapper) {
        return this.getService().viewResource(wrapper);
    }
}
