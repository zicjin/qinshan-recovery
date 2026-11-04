/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.resolver;

import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import java.util.function.Function;

public interface LimsCustomerDataResolveFunction
extends Function<LimsTemplateDataList<LimsTemplateDataMap<Object>>, LimsTemplateDataList<LimsTemplateDataMap<Object>>> {
    @Override
    default public LimsTemplateDataList<LimsTemplateDataMap<Object>> apply(LimsTemplateDataList<LimsTemplateDataMap<Object>> currentDataList) {
        return currentDataList;
    }
}
