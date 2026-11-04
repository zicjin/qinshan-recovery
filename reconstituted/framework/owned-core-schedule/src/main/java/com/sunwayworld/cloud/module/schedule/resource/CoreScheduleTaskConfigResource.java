/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.resource;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskLogBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.resolver.feign.annotation.FeignRequestParam;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface CoreScheduleTaskConfigResource
extends GenericCloudResource<CoreScheduleTaskConfigBean, String>,
GenericActivatableCloudResource<CoreScheduleTaskConfigBean, String> {
    @RequestMapping(value={"/{id}/schedule-task-logs/queries"}, method={RequestMethod.POST})
    public Pagination<CoreScheduleTaskLogBean> selectTaskLogPagination(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/schedule-task-logs/{logId}"}, method={RequestMethod.GET})
    public CoreScheduleTaskLogBean selectTaskLog(@PathVariable String var1, @PathVariable Long var2);

    @RequestMapping(value={"/action/cancel"}, method={RequestMethod.PUT})
    public void cancel(@RequestParam String var1, @RequestParam String var2);

    @RequestMapping(value={"/action/schedule"}, method={RequestMethod.PUT})
    public void schedule(@RequestParam String var1, @RequestParam String var2, @RequestParam String var3, @RequestParam String var4, @RequestParam String var5, @RequestParam LocalDateTime var6, @RequestParam LocalDateTime var7);

    @RequestMapping(value={"/action/delete-by-id-list"}, method={RequestMethod.DELETE})
    public void deleteByIdList(@RequestBody List<String> var1);

    @RequestMapping(value={"/select-list"}, method={RequestMethod.POST})
    public ArrayList<CoreScheduleTaskConfigBean> selectList(@FeignRequestParam List<CoreScheduleTaskConfigBean> var1, @FeignRequestParam List<String> var2, @FeignRequestParam List<String> var3);

    @RequestMapping(value={"/action/update"}, method={RequestMethod.PUT})
    public void update(@FeignRequestParam List<CoreScheduleTaskConfigBean> var1, @FeignRequestParam String var2);

    @RequestMapping(value={"/select-list-by-one-column-values"}, method={RequestMethod.POST})
    public ArrayList<CoreScheduleTaskConfigBean> selectListByOneColumnValues(@FeignRequestParam List<Long> var1, @FeignRequestParam String var2);

    @RequestMapping(value={"/select-list-by-filter"}, method={RequestMethod.POST})
    public ArrayList<CoreScheduleTaskConfigBean> selectListByFilter(@RequestBody SearchFilter var1);

    @RequestMapping(value={"/{id}"}, method={RequestMethod.DELETE})
    public void delete(@PathVariable String var1);

    @RequestMapping(value={"/action/update-if-changed"}, method={RequestMethod.PUT})
    public void updateIfChanged(@RequestBody CoreScheduleTaskConfigBean var1);

    @RequestMapping(value={"/action/insert"}, method={RequestMethod.POST})
    public void insert(@RequestBody CoreScheduleTaskConfigBean var1);

    @RequestMapping(value={"/action/delete-by"}, method={RequestMethod.DELETE})
    public void deleteBy(@RequestBody List<CoreScheduleTaskConfigBean> var1);
}
