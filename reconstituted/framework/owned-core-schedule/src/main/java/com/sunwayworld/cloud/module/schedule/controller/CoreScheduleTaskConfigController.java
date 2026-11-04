/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/schedule-task-configs/page"})
public interface CoreScheduleTaskConfigController
extends GenericDetailPageController,
GenericSearchListPageController {
}
