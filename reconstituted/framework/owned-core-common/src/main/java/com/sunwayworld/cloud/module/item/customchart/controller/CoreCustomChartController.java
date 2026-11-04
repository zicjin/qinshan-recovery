/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/custom-charts/page"})
public interface CoreCustomChartController
extends GenericEditListPageController,
GenericDetailPageController {
    @RequestMapping(value={"/display/panel"})
    public String displayPanelPage();

    @RequestMapping(value={"/display/display-menu"})
    public String displayMenulPage();
}
