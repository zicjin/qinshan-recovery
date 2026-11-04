/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.rounding.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/rounding-rules/page"})
public interface CoreRoundingRuleController
extends GenericEditListPageController,
GenericDetailPageController,
GenericChoosePageController {
    @RequestMapping(value={"/calculate-detail"})
    public String calculateDetailPage();
}
