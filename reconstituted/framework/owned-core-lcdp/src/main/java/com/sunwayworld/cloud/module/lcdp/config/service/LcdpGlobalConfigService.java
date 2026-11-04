/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.service;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpConfigCompareDTO;
import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalConfigBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface LcdpGlobalConfigService
extends GenericService<LcdpGlobalConfigBean, Long> {
    public LcdpGlobalConfigBean selectConfigContent(String var1);

    public void submit(RestJsonWrapperBean var1);

    public void submit(LcdpGlobalConfigBean var1);

    public void activate(RestJsonWrapperBean var1);

    public LcdpConfigCompareDTO compare(RestJsonWrapperBean var1);
}
