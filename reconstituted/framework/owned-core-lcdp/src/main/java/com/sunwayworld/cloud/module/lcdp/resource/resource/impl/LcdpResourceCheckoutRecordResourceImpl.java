/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.resource.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.resource.resource.LcdpResourceCheckoutRecordResource;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceCheckoutRecordService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u8d44\u6e90\u68c0\u51fa\u8bb0\u5f55\u8868")
@RestController
@GikamBean
public class LcdpResourceCheckoutRecordResourceImpl
implements LcdpResourceCheckoutRecordResource,
AbstractGenericResource<LcdpResourceCheckoutRecordService, LcdpResourceCheckoutRecordBean, Long> {
    @Autowired
    private LcdpResourceCheckoutRecordService lcdpResourceCheckoutRecordService;

    @Override
    public LcdpResourceCheckoutRecordService getService() {
        return this.lcdpResourceCheckoutRecordService;
    }
}
