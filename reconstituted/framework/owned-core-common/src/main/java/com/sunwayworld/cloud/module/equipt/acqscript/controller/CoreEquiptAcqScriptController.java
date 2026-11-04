/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqscript.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/equipt/acq-scripts/page"})
public interface CoreEquiptAcqScriptController
extends GenericEditListPageController,
GenericDetailPageController,
GenericChoosePageController {
}
