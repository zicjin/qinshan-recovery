/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/data-centers/page"})
public interface CoreDataCenterController
extends GenericEditListPageController,
GenericSearchListPageController {
    public String reportChooseListPage();

    public String userEditListPage();
}
