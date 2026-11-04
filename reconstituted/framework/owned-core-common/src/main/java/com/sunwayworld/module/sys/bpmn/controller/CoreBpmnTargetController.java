/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/bpmn-targets/page"})
public interface CoreBpmnTargetController {
    @RequestMapping(value={"/odd-list"})
    public String oddListPage();
}
