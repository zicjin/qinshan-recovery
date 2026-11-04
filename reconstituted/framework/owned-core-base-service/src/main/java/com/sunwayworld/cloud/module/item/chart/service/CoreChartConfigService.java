/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.service;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartConfigBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreChartConfigService
extends GenericService<CoreChartConfigBean, Long> {
    public Page<CoreChartConfigBean> selectPaginationByChart(String var1, RestJsonWrapperBean var2);

    default public Page<CoreChartConfigBean> selectPaginationByChart(String chartId) {
        return this.selectPaginationByChart(chartId, null);
    }
}
