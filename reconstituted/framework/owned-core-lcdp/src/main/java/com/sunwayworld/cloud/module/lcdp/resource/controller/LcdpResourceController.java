/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/lcdp/resources/page"})
public interface LcdpResourceController {
    public String pageChoosePage();

    public String methodChoosePage();
}
