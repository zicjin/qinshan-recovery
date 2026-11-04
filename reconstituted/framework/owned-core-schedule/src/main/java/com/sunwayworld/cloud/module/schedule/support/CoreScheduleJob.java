/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.support;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import java.io.Serializable;
import java.time.LocalDateTime;

public final class CoreScheduleJob
implements Serializable {
    private static final long serialVersionUID = -8553774284978126672L;
    private final CoreScheduleTaskConfigBean taskConfig;
    private LocalDateTime triggeredTime;

    public CoreScheduleJob(CoreScheduleTaskConfigBean taskConfig, LocalDateTime triggeredTime) {
        this.taskConfig = taskConfig;
        this.triggeredTime = triggeredTime;
    }

    public CoreScheduleTaskConfigBean getTaskConfig() {
        return this.taskConfig;
    }

    public LocalDateTime getTriggeredTime() {
        return this.triggeredTime;
    }

    public void setTriggeredTime(LocalDateTime triggeredTime) {
        this.triggeredTime = triggeredTime;
    }
}
