/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.service;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCheckoutRecordBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface LcdpResourceCheckoutRecordService
extends GenericService<LcdpResourceCheckoutRecordBean, Long> {
    public void checkoutResource(List<LcdpResourceBean> var1);

    public void unCheckoutResource(List<LcdpResourceBean> var1);

    public void checkoutTableOrView(String var1, String var2, String var3);

    public void unCheckoutTableOrView(List<String> var1);

    public void dealResourceCheckoutRecord();
}
