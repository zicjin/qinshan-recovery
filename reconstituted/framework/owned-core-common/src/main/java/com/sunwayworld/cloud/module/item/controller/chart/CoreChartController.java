/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.chart;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/item/charts/page"})
public interface CoreChartController
extends GenericEditListPageController,
GenericSearchListPageController,
GenericDetailPageController {
    public String displayPage();

    public String maintainListPage();

    public String echarts();
}
