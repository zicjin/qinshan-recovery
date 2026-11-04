/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.controller.impl;

import com.sunwayworld.cloud.module.schedule.controller.CoreScheduleTaskLogController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreScheduleTaskLogControllerImpl
implements CoreScheduleTaskLogController {
    @Override
    @Log(value="\u5b9a\u65f6\u5668\u4efb\u52a1\u8be6\u7ec6\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/sys/schedule-task-logs/core-schedule-task-log-detail";
    }
}
