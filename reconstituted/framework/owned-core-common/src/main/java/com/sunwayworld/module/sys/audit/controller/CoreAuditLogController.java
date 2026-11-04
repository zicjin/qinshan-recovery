/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.controller;

import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/audit/audit-logs/page"})
public interface CoreAuditLogController
extends GenericSearchListPageController {
    public String recordDetailPage();

    @RequestMapping(value={"/trace-list"})
    public String traceListPage();

    @RequestMapping(value={"/query-list"})
    public String queryListPage();
}
