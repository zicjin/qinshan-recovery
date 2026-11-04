/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/code-categories/page"})
public interface CoreCodeCategoryController
extends GenericEditListPageController,
GenericDetailPageController {
}
