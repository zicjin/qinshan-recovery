/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.controller;

import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/at-logs/page"})
public interface CoreAuditTrailLogController
extends GenericSearchListPageController {
    @RequestMapping(value={"/trace-list"})
    public String traceListPage();
}
