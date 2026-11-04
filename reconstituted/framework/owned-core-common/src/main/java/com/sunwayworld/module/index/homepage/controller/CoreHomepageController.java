/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.index.homepage.controller;

import com.sunwayworld.framework.controller.GenericIndexPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/"})
public interface CoreHomepageController
extends GenericIndexPageController {
    @RequestMapping(value={"/open/home-resource-page"})
    public String resourcePage();
}
