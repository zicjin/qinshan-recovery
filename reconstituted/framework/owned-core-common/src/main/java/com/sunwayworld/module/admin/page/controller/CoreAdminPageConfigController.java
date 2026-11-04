/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/admin/page-configs/page"})
public interface CoreAdminPageConfigController
extends GenericEditListPageController {
    @RequestMapping(value={"/design"})
    public String designPage();
}
