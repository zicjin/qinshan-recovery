/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.calendar.resource;

import com.sunwayworld.cloud.module.item.calendar.bean.CoreCalendarBean;
import com.sunwayworld.cloud.module.item.calendar.bean.CoreCalendarRepresentation;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value={"/core/module/item/calendars"})
public interface CoreCalendarResource
extends GenericCloudResource<CoreCalendarBean, String> {
    @RequestMapping(value={"/representations"}, method={RequestMethod.POST})
    public ArrayList<CoreCalendarRepresentation> selectCalendarList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{year}/{month}/{dayOfMonth}/{dayRepresentation}"}, method={RequestMethod.POST})
    @ResponseStatus(value=HttpStatus.OK)
    public void save(@PathVariable int var1, @PathVariable int var2, @PathVariable int var3, @PathVariable String var4);

    @RequestMapping(value={"/work-days"}, method={RequestMethod.POST})
    public Long getWorkDays(RestJsonWrapperBean var1);
}
