/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.equipt.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/equipt/equipts/page"})
public interface CoreEquiptController
extends GenericEditListPageController,
GenericDetailPageController {
    @RequestMapping(value={"/monitor"})
    public String monitorPage();
}
