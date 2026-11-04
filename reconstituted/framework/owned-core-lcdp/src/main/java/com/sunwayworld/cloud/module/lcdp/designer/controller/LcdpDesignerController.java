/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.designer.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/lcdp/designers/page"})
public interface LcdpDesignerController {
    public String indexPage();

    public String runPage();
}
