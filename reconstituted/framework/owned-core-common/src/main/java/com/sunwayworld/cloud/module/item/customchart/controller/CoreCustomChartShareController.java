/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/custom-charts-shares/page"})
public interface CoreCustomChartShareController {
    @RequestMapping(value={"/display/link"})
    public String displayLinkPage();
}
