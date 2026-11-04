/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/custom-queries/page"})
public interface CoreCustomQueryController
extends GenericEditListPageController,
GenericDetailPageController {
    @RequestMapping(value={"/field-choose-list"})
    public String fieldChoosePage();

    @RequestMapping(value={"/grid-menu-list"})
    public String gridMenuPage();

    @RequestMapping(value={"/report-menu-list"})
    public String reportMenuPage();
}
