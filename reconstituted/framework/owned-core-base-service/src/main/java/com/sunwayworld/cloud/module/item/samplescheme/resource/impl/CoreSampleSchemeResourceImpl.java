/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.resource.impl;

import com.microsoft.sqlserver.jdbc.StringUtils;
import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeBean;
import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeDTO;
import com.sunwayworld.cloud.module.item.samplescheme.resource.CoreSampleSchemeResource;
import com.sunwayworld.cloud.module.item.samplescheme.service.CoreSampleSchemeService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u62bd\u6837\u65b9\u6848")
@RestController
@GikamBean
public class CoreSampleSchemeResourceImpl
implements CoreSampleSchemeResource,
AbstractGenericResource<CoreSampleSchemeService, CoreSampleSchemeBean, Long> {
    @Autowired
    private CoreSampleSchemeService sampleSchemeService;

    @Override
    public CoreSampleSchemeService getService() {
        return this.sampleSchemeService;
    }

    @Override
    public CoreSampleSchemeDTO selectSampleScheme(RestJsonWrapperBean wrapper) {
        String batchQtyStr = wrapper.getFilterValue("batchQty");
        String inspection = wrapper.getFilterValue("inspection");
        String aql = wrapper.getFilterValue("aql");
        if (NumberUtils.isNumber(batchQtyStr) && NumberUtils.isNumber(aql) && !StringUtils.isEmpty(inspection)) {
            return this.sampleSchemeService.selectSampleScheme(Long.parseLong(batchQtyStr), inspection, Double.parseDouble(aql));
        }
        return null;
    }
}
