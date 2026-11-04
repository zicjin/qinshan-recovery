/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/menu-groups/page"})
public interface CoreMenuGroupController
extends GenericEditListPageController,
GenericDetailPageController,
GenericChoosePageController {
}
