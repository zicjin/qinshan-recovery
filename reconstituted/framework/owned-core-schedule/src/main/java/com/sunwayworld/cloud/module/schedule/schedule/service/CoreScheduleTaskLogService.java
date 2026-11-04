/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.service;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreScheduleTaskLogService
extends GenericService<CoreScheduleTaskLogBean, Long> {
    public Page<CoreScheduleTaskLogBean> selectPagination(String var1, RestJsonWrapperBean var2);

    public void update(List<CoreScheduleTaskLogBean> var1, String ... var2);
}
