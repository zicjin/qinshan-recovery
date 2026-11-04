/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.service;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.time.LocalDateTime;

public interface CoreScheduleTaskService
extends GenericService<CoreScheduleTaskBean, String>,
GenericChoosableService<CoreScheduleTaskBean, String> {
    public Boolean executableTask(String var1, String var2, LocalDateTime var3);

    public String runTask(String var1, String var2, LocalDateTime var3);

    public void saveScript(RestJsonWrapperBean var1);
}
