/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.resource;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/custom-charts"})
public interface CoreCustomChartResource
extends GenericCloudResource<CoreCustomChartBean, Long> {
    public void revoke(RestJsonWrapperBean var1);

    public void publishToMenu(RestJsonWrapperBean var1);

    public void publishToWKPanel(RestJsonWrapperBean var1);

    public void copy(RestJsonWrapperBean var1);

    public void modify(RestJsonWrapperBean var1);
}
