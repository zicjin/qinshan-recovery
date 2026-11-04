/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.support;

import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskConfigService;
import com.sunwayworld.cloud.module.schedule.support.CoreScheduleJob;
import com.sunwayworld.cloud.module.schedule.support.CoreScheduleJobContainer;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamGuardianLocker;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.loop.registry.LoopRunner;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.CronUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class CoreScheduleLoopRunner
implements LoopRunner {
    @Override
    public void run() {
        GikamGuardianLocker.isolatedRun("CORE_SCHEDULE_TASK_LOOP_RUNNER", () -> {
            CoreScheduleTaskConfigService configService = ApplicationContextHelper.getBean(CoreScheduleTaskConfigService.class);
            Map<String, CoreScheduleJob> jobMap = CoreScheduleJobContainer.INSTANCE.getJobMap();
            if (jobMap.isEmpty()) {
                return;
            }
            ArrayList removeKeyList = new ArrayList();
            long seconds = System.currentTimeMillis() / 1000L + 1L;
            jobMap.forEach((k, v) -> {
                LocalDateTime triggeredTime = v.getTriggeredTime();
                if (triggeredTime != null) {
                    long triggeredMilliSeconds = ConvertUtils.convert(triggeredTime, Long.TYPE);
                    if (triggeredMilliSeconds < seconds * 1000L) {
                        ExecutorHelper.submit(() -> configService.scheduleTask(v.getTaskConfig().getId(), triggeredTime));
                        v.setTriggeredTime(CronUtils.getNextExecutionTime(v.getTaskConfig().getCron(), triggeredTime));
                    }
                } else {
                    removeKeyList.add(k);
                }
            });
            removeKeyList.forEach(k -> {
                CoreScheduleJob cfr_ignored_0 = (CoreScheduleJob)jobMap.remove(k);
            });
            CoreScheduleJobContainer.INSTANCE.setJobMap(jobMap, true);
        });
    }

    @Override
    public boolean active() {
        return CoreScheduleJobContainer.INSTANCE.getScheduleJobSize() > 0;
    }

    @Override
    public boolean enable() {
        return ApplicationContextHelper.isProfileActivated("schedule");
    }
}
