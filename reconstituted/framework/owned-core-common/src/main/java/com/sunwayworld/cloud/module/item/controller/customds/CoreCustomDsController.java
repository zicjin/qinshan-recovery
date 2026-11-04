/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.customds;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/custom-dses/page"})
public interface CoreCustomDsController
extends GenericEditListPageController,
GenericDetailPageController,
GenericChoosePageController {
    @RequestMapping(value={"/add-list"})
    public String addListPage();
}
