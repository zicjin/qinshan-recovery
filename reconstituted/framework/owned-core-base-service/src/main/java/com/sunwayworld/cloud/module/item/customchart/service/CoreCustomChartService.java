/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.service;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreCustomChartService
extends GenericService<CoreCustomChartBean, Long>,
GenericChoosableService<CoreCustomChartBean, Long> {
    public void revoke(RestJsonWrapperBean var1);

    public void publishToMenu(RestJsonWrapperBean var1);

    public void publishToWKPanel(RestJsonWrapperBean var1);

    public void copy(RestJsonWrapperBean var1);

    public void modify(RestJsonWrapperBean var1);
}
