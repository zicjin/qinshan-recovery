/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqconfig.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/equipt/acq-configs/page"})
public interface CoreEquiptAcqConfigController {
    @RequestMapping(value={"/connect-list"})
    public String connectListPage();
}
