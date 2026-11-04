/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.resource.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskLogBean;
import com.sunwayworld.cloud.module.schedule.resource.CoreScheduleTaskLogResource;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskLogService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.resolver.feign.annotation.FeignRequestParam;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5b9a\u65f6\u4efb\u52a1\u65e5\u5fd7")
@GikamBean
@RequestMapping(value={"/secure/core/module/sys/schedule-task-logs"})
public class CoreScheduleTaskLogResourceImpl
implements CoreScheduleTaskLogResource,
AbstractGenericResource<CoreScheduleTaskLogService, CoreScheduleTaskLogBean, Long> {
    @Autowired
    private CoreScheduleTaskLogService logService;

    @Override
    public CoreScheduleTaskLogService getService() {
        return this.logService;
    }

    @Override
    public List<CoreScheduleTaskLogBean> selectListByFilter(@FeignRequestParam SearchFilter searchFilter, @FeignRequestParam RestJsonWrapperBean wrapper) {
        return this.getService().selectListByFilter(searchFilter, wrapper);
    }

    @Override
    public void update(@FeignRequestParam List<CoreScheduleTaskLogBean> coreScheduleTaskLogList, @FeignRequestParam String updateColName) {
        this.getService().update(coreScheduleTaskLogList, updateColName);
    }
}
