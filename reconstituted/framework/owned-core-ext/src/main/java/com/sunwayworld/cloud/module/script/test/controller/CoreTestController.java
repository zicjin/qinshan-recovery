/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.test.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/script/tests/page"})
public interface CoreTestController
extends GenericEditListPageController {
    @RequestMapping(value={"/item-script-detail"})
    public String itemScriptDetailPage();

    @RequestMapping(value={"/item-script-selector"})
    public String itemScriptSelectorPage();
}
