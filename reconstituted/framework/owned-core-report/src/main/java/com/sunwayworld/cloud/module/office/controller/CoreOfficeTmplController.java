/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/office/tmpls/page"})
public interface CoreOfficeTmplController
extends GenericEditListPageController,
GenericDetailPageController,
GenericChoosePageController {
    @RequestMapping(value={"/test-tmpl"})
    public String testTmplPage();
}
