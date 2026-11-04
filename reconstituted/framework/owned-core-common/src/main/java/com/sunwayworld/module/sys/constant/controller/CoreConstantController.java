/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.constant.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/constants/page"})
public interface CoreConstantController
extends GenericEditListPageController,
GenericDetailPageController {
}
