/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.iframe.controller;

import com.sunwayworld.framework.controller.GenericIndexPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/item/iframes"})
public interface CoreIframeController
extends GenericIndexPageController {
    @RequestMapping(value={"/common-single"})
    public String commonSinglePage();
}
