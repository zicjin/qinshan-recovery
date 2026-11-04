/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.support.helper;

import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeDTO;
import com.sunwayworld.cloud.module.item.samplescheme.service.CoreSampleSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreSampleSchemeHelper {
    private static CoreSampleSchemeService sampleSchemeService;

    @Autowired
    public void setMessageService(CoreSampleSchemeService sampleSchemeService) {
        CoreSampleSchemeHelper.sampleSchemeService = sampleSchemeService;
    }

    public static CoreSampleSchemeDTO selectSampleScheme(Long batchQty, String inspection, Double aql) {
        return sampleSchemeService.selectSampleScheme(batchQty, inspection, aql);
    }
}
