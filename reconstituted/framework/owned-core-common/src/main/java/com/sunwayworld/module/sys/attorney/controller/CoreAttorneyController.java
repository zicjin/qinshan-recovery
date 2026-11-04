/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/attorneys/page"})
public interface CoreAttorneyController
extends GenericEditListPageController,
GenericSearchListPageController {
}
