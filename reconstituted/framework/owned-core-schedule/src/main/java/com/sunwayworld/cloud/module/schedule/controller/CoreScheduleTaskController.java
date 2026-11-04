/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/schedule-tasks/page"})
public interface CoreScheduleTaskController
extends GenericEditListPageController,
GenericDetailPageController,
GenericSearchListPageController {
    @RequestMapping(value={"/maintain-list"})
    public String maintainListPage();

    @RequestMapping(value={"/script-edit"})
    public String scriptEditPage();
}
