/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.resource.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskLogBean;
import com.sunwayworld.cloud.module.schedule.resource.CoreScheduleTaskConfigResource;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleManager;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskConfigService;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskLogService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.resolver.feign.annotation.FeignRequestParam;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5b9a\u65f6\u4efb\u52a1\u914d\u7f6e")
@GikamBean
@RequestMapping(value={"/secure/core/module/sys/schedule-task-configs"})
public class CoreScheduleTaskConfigResourceImpl
implements CoreScheduleTaskConfigResource,
AbstractGenericResource<CoreScheduleTaskConfigService, CoreScheduleTaskConfigBean, String>,
AbstractGenericActivatableResource<CoreScheduleTaskConfigService, CoreScheduleTaskConfigBean, String> {
    @Autowired
    private CoreScheduleTaskConfigService configService;
    @Autowired
    private CoreScheduleTaskLogService logService;
    @Autowired
    private CoreScheduleManager coreScheduleManager;

    @Override
    public CoreScheduleTaskConfigService getService() {
        return this.configService;
    }

    @Override
    @Log(value="\u901a\u8fc7\u5b9a\u65f6\u4efb\u52a1\u67e5\u8be2\u6267\u884c\u65e5\u5fd7", type=LogType.SELECT)
    public Pagination<CoreScheduleTaskLogBean> selectTaskLogPagination(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.logService.selectPaginationByFilter(SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5b9a\u65f6\u4efb\u52a1\u6267\u884c\u65e5\u5fd7\u8be6\u7ec6\u4fe1\u606f", type=LogType.SELECT)
    public CoreScheduleTaskLogBean selectTaskLog(@PathVariable String id, @PathVariable Long logId) {
        return (CoreScheduleTaskLogBean)this.logService.selectDetail(logId);
    }

    @Override
    public void cancel(@RequestParam String taskId, @RequestParam String uid) {
        this.coreScheduleManager.cancel(taskId, uid);
    }

    @Override
    public void schedule(@RequestParam String taskId, @RequestParam String uid, @RequestParam String configName, @RequestParam String cron, @RequestParam String param, @RequestParam LocalDateTime startTime, @RequestParam LocalDateTime endTime) {
        this.coreScheduleManager.schedule(taskId, uid, configName, cron, param, startTime, endTime);
    }

    @Override
    public void deleteByIdList(@RequestBody List<String> coreScheduleTaskConfigIdList) {
        this.getService().deleteByIdList(coreScheduleTaskConfigIdList);
    }

    @Override
    public ArrayList<CoreScheduleTaskConfigBean> selectList(@FeignRequestParam List<CoreScheduleTaskConfigBean> coreScheduleTaskConfigBeanTempList, @FeignRequestParam List<String> searchColNames, @FeignRequestParam List<String> selectColNames) {
        return (ArrayList)this.getService().selectList(coreScheduleTaskConfigBeanTempList, searchColNames, selectColNames);
    }

    @Override
    public void update(@FeignRequestParam List<CoreScheduleTaskConfigBean> configList, @FeignRequestParam String updateColName) {
        this.getService().update(configList, new String[0]);
    }

    @Override
    public ArrayList<CoreScheduleTaskConfigBean> selectListByOneColumnValues(@FeignRequestParam List<Long> limsEquiptEventScheduleIdList, @FeignRequestParam String colName) {
        return (ArrayList)this.getService().selectListByOneColumnValues(limsEquiptEventScheduleIdList, colName);
    }

    @Override
    public ArrayList<CoreScheduleTaskConfigBean> selectListByFilter(@RequestBody SearchFilter param) {
        return (ArrayList)this.getService().selectListByFilter(param, new Order[0]);
    }

    @Override
    public void updateIfChanged(@RequestBody CoreScheduleTaskConfigBean taskConfigBean) {
        this.getService().updateIfChanged(taskConfigBean);
    }

    @Override
    public void insert(@RequestBody CoreScheduleTaskConfigBean coreScheduleTaskConfig) {
        this.getService().insert(coreScheduleTaskConfig);
    }

    @Override
    public void deleteBy(@RequestBody List<CoreScheduleTaskConfigBean> taskConfigBeans) {
        this.getService().deleteBy(taskConfigBeans);
    }

    @Override
    public void delete(@PathVariable String id) {
        this.getService().delete(id);
    }
}
