/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourceversion.resource.impl;

import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.resourceversion.resource.LcdpResourceVersionResource;
import com.sunwayworld.cloud.module.lcdp.resourceversion.service.LcdpResourceVersionService;
import com.sunwayworld.cloud.module.lcdp.resourceversion.validator.LcdpResourceActivateableValidator;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u8d44\u6e90\u7248\u672c")
@RestController
@GikamBean
public class LcdpResourceVersionResourceImpl
implements LcdpResourceVersionResource,
AbstractGenericResource<LcdpResourceVersionService, LcdpResourceVersionBean, Long> {
    @Autowired
    private LcdpResourceVersionService lcdpResourceVersionService;

    @Override
    public LcdpResourceVersionService getService() {
        return this.lcdpResourceVersionService;
    }

    @Override
    @Log(value="\u8d44\u6e90\u6fc0\u6d3b", type=LogType.UPDATE)
    @ValidateDataWith(value=LcdpResourceActivateableValidator.class)
    public void activateResource(RestJsonWrapperBean wrapper) {
        this.getService().activateResource(wrapper);
    }

    @Override
    @Log(value="\u5386\u53f2\u6570\u636e\u5904\u7406(\u8865\u5168\u7248\u672c\u8868\u66f4\u65b0\u540e\u90e8\u5206\u5b57\u6bb5\u503c\u7f3a\u5931\u95ee\u9898)", type=LogType.UPDATE)
    public void dealHistoryData(RestJsonWrapperBean wrapper) {
        this.getService().dealHistoryData(wrapper);
    }
}
