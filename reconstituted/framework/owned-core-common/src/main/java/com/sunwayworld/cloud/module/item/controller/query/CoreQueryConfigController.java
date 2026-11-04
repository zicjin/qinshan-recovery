/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.query;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/query-configs/page"})
public interface CoreQueryConfigController
extends GenericEditListPageController,
GenericDetailPageController,
GenericSearchListPageController {
    @RequestMapping(value={"/single-search-list"})
    public String singleSearchListPage();
}
