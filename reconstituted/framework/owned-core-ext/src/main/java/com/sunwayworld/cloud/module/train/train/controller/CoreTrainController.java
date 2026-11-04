/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.train.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/train/trains/page"})
public interface CoreTrainController
extends GenericEditListPageController,
GenericSearchListPageController,
GenericDetailPageController {
    @RequestMapping(value={"/user-search"})
    public String userSearchPage();
}
