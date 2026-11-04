/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.resource;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskLogBean;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.resolver.feign.annotation.FeignRequestParam;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreScheduleTaskLogResource
extends GenericCloudResource<CoreScheduleTaskLogBean, Long> {
    @RequestMapping(value={"/selectListByFilter"}, method={RequestMethod.POST})
    public List<CoreScheduleTaskLogBean> selectListByFilter(@FeignRequestParam SearchFilter var1, @FeignRequestParam RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/update"}, method={RequestMethod.PUT})
    public void update(@FeignRequestParam List<CoreScheduleTaskLogBean> var1, @FeignRequestParam String var2);
}
