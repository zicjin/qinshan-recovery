/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.service;

import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleQtyCodeBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreSampleQtyCodeService
extends GenericService<CoreSampleQtyCodeBean, Long> {
    public String selectSampleQtyCode(Long var1, String var2);
}
