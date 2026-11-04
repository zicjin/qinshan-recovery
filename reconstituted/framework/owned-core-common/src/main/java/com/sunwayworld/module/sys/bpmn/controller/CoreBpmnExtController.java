/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/bpmn-exts/page"})
public interface CoreBpmnExtController {
    @RequestMapping(value={"/process-status-detail"})
    public String processStatusDetailPage();

    @RequestMapping(value={"/runtime-target-list"})
    public String runtimeTargetList();
}
