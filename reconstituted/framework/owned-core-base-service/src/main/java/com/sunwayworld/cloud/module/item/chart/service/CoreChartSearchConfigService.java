/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.service;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartSearchConfigBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreChartSearchConfigService
extends GenericService<CoreChartSearchConfigBean, Long> {
    public Page<CoreChartSearchConfigBean> selectPaginationByChart(String var1, RestJsonWrapperBean var2);

    default public Page<CoreChartSearchConfigBean> selectPaginationByChart(String chartId) {
        return this.selectPaginationByChart(chartId, null);
    }
}
