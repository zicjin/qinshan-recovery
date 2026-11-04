/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.controller.impl;

import com.sunwayworld.cloud.module.schedule.controller.CoreScheduleTaskConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreScheduleTaskConfigControllerImpl
implements CoreScheduleTaskConfigController {
    @Override
    @Log(value="\u5b9a\u65f6\u5668\u4efb\u52a1\u8be6\u7ec6\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/sys/schedule-task-configs/core-schedule-task-config-detail";
    }

    @Override
    @Log(value="\u5b9a\u65f6\u5668\u4efb\u52a1\u6267\u884c\u60c5\u51b5\u67e5\u8be2\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/sys/schedule-task-configs/core-schedule-task-config-search-list";
    }
}
