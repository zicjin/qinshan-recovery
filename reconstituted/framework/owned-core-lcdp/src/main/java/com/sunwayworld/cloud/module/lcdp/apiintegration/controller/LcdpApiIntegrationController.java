/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/lcdp/api-integrations/page"})
public interface LcdpApiIntegrationController
extends GenericEditListPageController {
    @RequestMapping(value={"/log-list"})
    public String logPage();
}
