/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.resource.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskBean;
import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import com.sunwayworld.cloud.module.schedule.resource.CoreScheduleTaskResource;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskConfigService;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskService;
import com.sunwayworld.cloud.module.schedule.schedule.validator.CoreScheduleTaskSaveValidator;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5b9a\u65f6\u4efb\u52a1")
@GikamBean
@RequestMapping(value={"/secure/core/module/sys/schedule-tasks"})
public class CoreScheduleTaskResourceImpl
implements CoreScheduleTaskResource,
AbstractGenericResource<CoreScheduleTaskService, CoreScheduleTaskBean, String>,
AbstractGenericChoosableResource<CoreScheduleTaskService, CoreScheduleTaskBean, String> {
    @Autowired
    private CoreScheduleTaskService taskService;
    @Autowired
    private CoreScheduleTaskConfigService configService;

    @Override
    public CoreScheduleTaskService getService() {
        return this.taskService;
    }

    @Override
    @Log(value="\u901a\u8fc7\u5b9a\u65f6\u4efb\u52a1\u751f\u6210\u5b9a\u65f6\u4efb\u52a1\u914d\u7f6e\u4fe1\u606f", type=LogType.INSERT)
    public String insertTaskConfig(@PathVariable String id) {
        return this.configService.insertByTask(id);
    }

    @Override
    @Log(value="\u901a\u8fc7\u5b9a\u65f6\u4efb\u52a1\u67e5\u8be2\u4efb\u52a1\u914d\u7f6e\u4fe1\u606f", type=LogType.SELECT)
    public Pagination<CoreScheduleTaskConfigBean> selectTaskConfigPagination(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.configService.selectPaginationByFilter(SearchFilter.instance().match("TASKID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void runTask(@RequestParam String taskId, @RequestParam String param, @RequestParam LocalDateTime createdTime) {
        this.getService().runTask(taskId, param, createdTime);
    }

    @Override
    @Log(value="\u4fee\u6539\u5b9a\u65f6\u4efb\u52a1\u6267\u884c\u811a\u672c", type=LogType.UPDATE)
    @ValidateDataWith(value=CoreScheduleTaskSaveValidator.class)
    public void saveScript(RestJsonWrapperBean wrapper) {
        this.getService().saveScript(wrapper);
    }
}
