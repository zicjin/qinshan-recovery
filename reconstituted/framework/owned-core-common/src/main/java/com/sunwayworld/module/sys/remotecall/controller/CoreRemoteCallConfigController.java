/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/remote-calls/page"})
public interface CoreRemoteCallConfigController
extends GenericEditListPageController {
    @RequestMapping(value={"/request-detail"})
    public String requestDetailPage();
}
