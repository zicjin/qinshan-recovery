/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/les/les-dses/page"})
public interface CoreLesDsController
extends GenericEditListPageController,
GenericDetailPageController,
GenericChoosePageController {
}
