/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/tables/page"})
public interface CoreTableController
extends GenericChoosePageController {
    @RequestMapping(value={"/column-choose-list"})
    public String columnChoosePage();
}
