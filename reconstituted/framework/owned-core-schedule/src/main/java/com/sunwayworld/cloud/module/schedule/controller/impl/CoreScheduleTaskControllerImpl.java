/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.controller.impl;

import com.sunwayworld.cloud.module.schedule.controller.CoreScheduleTaskController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreScheduleTaskControllerImpl
implements CoreScheduleTaskController {
    @Override
    @Log(value="\u5b9a\u65f6\u5668\u4efb\u52a1\u914d\u7f6e\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/schedule-tasks/core-schedule-task-edit-list";
    }

    @Override
    @Log(value="\u5b9a\u65f6\u5668\u4efb\u52a1\u8be6\u7ec6\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/sys/schedule-tasks/core-schedule-task-detail";
    }

    @Override
    @Log(value="\u5b9a\u65f6\u5668\u4efb\u52a1\u67e5\u8be2\u9875\u9762", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/sys/schedule-tasks/core-schedule-task-search-list";
    }

    @Override
    @Log(value="\u5b9a\u65f6\u5668\u4efb\u52a1\u7ef4\u62a4\u9875\u9762", type=LogType.CONTROLLER)
    public String maintainListPage() {
        return "module/sys/schedule-tasks/core-schedule-task-maintain-list";
    }

    @Override
    @Log(value="\u5b9a\u65f6\u5668\u4efb\u52a1\u811a\u672c\u7f16\u8f91\u9875\u9762", type=LogType.CONTROLLER)
    public String scriptEditPage() {
        return "module/sys/schedule-tasks/core-schedule-task-script-edit";
    }
}
