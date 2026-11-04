/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.support.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskConfigService;
import com.sunwayworld.cloud.module.schedule.support.CoreScheduleJob;
import com.sunwayworld.cloud.module.schedule.support.CoreScheduleJobContainer;
import com.sunwayworld.cloud.module.schedule.support.CoreScheduleScheduleService;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.utils.CronUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile(value={"schedule"})
public class CoreScheduleScheduleServiceImpl
implements CoreScheduleScheduleService {
    @Autowired
    private CoreScheduleTaskConfigService configService;
    @Autowired
    private TenantManager tenantManager;

    @Override
    @Transactional
    @Scheduled(fixedDelay=10000L)
    public void updateJobSchedule() {
        GikamConcurrentLocker.isolated("REFRESH_SCHEDULE_TASK_KEY");
        this.tenantManager.call("JOB_SCHEDULE", () -> {
            CoreScheduleTaskConfigBean filter = new CoreScheduleTaskConfigBean();
            filter.setActivatedFlag("1");
            List<CoreScheduleTaskConfigBean> configList = this.configService.selectList(filter, new Order[0]);
            Map<String, CoreScheduleJob> jobMap = CoreScheduleJobContainer.INSTANCE.getJobMap();
            if (!configList.isEmpty()) {
                for (CoreScheduleTaskConfigBean config : configList) {
                    LocalDateTime nextExecutionTime;
                    CoreScheduleJob scheduleJob = jobMap.get(config.getId());
                    if (scheduleJob == null) {
                        nextExecutionTime = CronUtils.getNextExecutionTime(config.getCron(), config.getStartTime(), config.getEndTime());
                        if (nextExecutionTime == null) continue;
                        jobMap.put(config.getId(), new CoreScheduleJob(config, nextExecutionTime));
                        continue;
                    }
                    if (!scheduleJob.getTaskConfig().updated(config)) continue;
                    nextExecutionTime = CronUtils.getNextExecutionTime(config.getCron(), config.getStartTime(), config.getEndTime());
                    if (nextExecutionTime != null) {
                        jobMap.put(config.getId(), new CoreScheduleJob(config, nextExecutionTime));
                        continue;
                    }
                    jobMap.remove(config.getId());
                }
                ArrayList<String> runningTaskConfigIdList = new ArrayList<String>(jobMap.keySet());
                for (String runningTaskConfigId : runningTaskConfigIdList) {
                    if (!configList.stream().noneMatch(c -> c.getId().equals(runningTaskConfigId))) continue;
                    jobMap.remove(runningTaskConfigId);
                }
                CoreScheduleJobContainer.INSTANCE.setJobMap(jobMap, false);
            } else if (!jobMap.isEmpty()) {
                CoreScheduleJobContainer.INSTANCE.clear();
            }
        }, new String[0]);
    }
}
