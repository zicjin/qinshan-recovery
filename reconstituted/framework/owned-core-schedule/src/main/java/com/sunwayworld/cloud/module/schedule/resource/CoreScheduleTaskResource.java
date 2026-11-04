/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.resource;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskBean;
import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface CoreScheduleTaskResource
extends GenericCloudResource<CoreScheduleTaskBean, String>,
GenericChoosableCloudResource<CoreScheduleTaskBean, String> {
    @RequestMapping(value={"/{id}/schedule-task-configs"}, method={RequestMethod.POST})
    public String insertTaskConfig(@PathVariable String var1);

    @RequestMapping(value={"/{id}/schedule-task-configs/queries"}, method={RequestMethod.POST})
    public Pagination<CoreScheduleTaskConfigBean> selectTaskConfigPagination(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/runTask"}, method={RequestMethod.PUT})
    public void runTask(@RequestParam String var1, @RequestParam String var2, @RequestParam LocalDateTime var3);

    @RequestMapping(value={"/action/save-script"}, method={RequestMethod.PUT})
    public void saveScript(RestJsonWrapperBean var1);
}
