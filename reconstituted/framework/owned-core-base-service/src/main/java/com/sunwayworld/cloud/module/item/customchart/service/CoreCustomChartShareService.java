/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.service;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartShareBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreCustomChartShareService
extends GenericService<CoreCustomChartShareBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public List<CoreCustomChartShareBean> selectCoreCustomChartShareBeanByChartId(RestJsonWrapperBean var1);
}
