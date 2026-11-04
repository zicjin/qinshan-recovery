/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/eln/eln-tmpls/page"})
public interface CoreElnTmplController
extends GenericEditListPageController,
GenericDetailPageController,
GenericChoosePageController {
    @RequestMapping(value={"/test-tmpl"})
    public String testTmplPage();
}
