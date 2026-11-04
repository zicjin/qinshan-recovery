/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/eln/elns/page"})
public interface CoreElnController {
    @RequestMapping(value={"/spreadjs-web-designer"})
    public String spreadJsWebDesignerPage();

    @RequestMapping(value={"/bind-data-source"})
    public String bindDataSourcePage();
}
