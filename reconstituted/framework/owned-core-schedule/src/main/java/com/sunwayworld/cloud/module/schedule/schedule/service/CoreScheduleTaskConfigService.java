/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.service;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.time.LocalDateTime;
import java.util.List;

public interface CoreScheduleTaskConfigService
extends GenericService<CoreScheduleTaskConfigBean, String>,
GenericActivatableService<CoreScheduleTaskConfigBean, String> {
    public String insertByTask(String var1);

    public void scheduleTask(String var1, LocalDateTime var2);

    public void updateTaskConfigTimes(CoreScheduleTaskConfigBean var1);

    public void deleteByIdList(List<String> var1);

    public List<CoreScheduleTaskConfigBean> selectList(List<CoreScheduleTaskConfigBean> var1, List<String> var2, List<String> var3);

    public void update(List<CoreScheduleTaskConfigBean> var1, String ... var2);

    public List<CoreScheduleTaskConfigBean> selectListByOneColumnValues(List<Long> var1, String var2);

    public void insert(CoreScheduleTaskConfigBean var1);

    public void deleteBy(List<CoreScheduleTaskConfigBean> var1);
}
