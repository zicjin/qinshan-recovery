/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.service;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartBean;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartLinesRepresentation;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import java.util.Map;

public interface CoreChartService
extends GenericService<CoreChartBean, String>,
GenericSelectableService<CoreChartBean, String> {
    @Override
    public String insert(RestJsonWrapperBean var1);

    public Long insertConfig(String var1, RestJsonWrapperBean var2);

    public void saveConfig(String var1, RestJsonWrapperBean var2);

    public void deleteConfig(String var1, RestJsonWrapperBean var2);

    public Long insertSearchConfig(String var1, RestJsonWrapperBean var2);

    public void deleteSearchConfig(String var1, RestJsonWrapperBean var2);

    public void submit(String var1);

    public Page<Map<String, Object>> selectMapPaginationByChartSql(String var1, RestJsonWrapperBean var2);

    public CoreChartLinesRepresentation packageLinesByConfig(String var1);
}
