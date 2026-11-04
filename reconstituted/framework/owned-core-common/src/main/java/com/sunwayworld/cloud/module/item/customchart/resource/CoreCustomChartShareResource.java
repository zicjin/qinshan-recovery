/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.resource;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartShareBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/custom-charts-shares"})
public interface CoreCustomChartShareResource
extends GenericCloudResource<CoreCustomChartShareBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public List<CoreCustomChartShareBean> selectCoreCustomChartShareBeanByChartId(RestJsonWrapperBean var1);
}
