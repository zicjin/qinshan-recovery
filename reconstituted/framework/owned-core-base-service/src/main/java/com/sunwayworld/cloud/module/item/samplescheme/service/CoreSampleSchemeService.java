/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.service;

import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeBean;
import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeDTO;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreSampleSchemeService
extends GenericService<CoreSampleSchemeBean, Long> {
    public CoreSampleSchemeDTO selectSampleScheme(Long var1, String var2, Double var3);
}
