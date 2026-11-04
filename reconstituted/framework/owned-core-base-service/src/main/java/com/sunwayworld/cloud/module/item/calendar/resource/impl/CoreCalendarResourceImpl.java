/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.calendar.resource.impl;

import com.sunwayworld.cloud.module.item.calendar.bean.CoreCalendarBean;
import com.sunwayworld.cloud.module.item.calendar.bean.CoreCalendarRepresentation;
import com.sunwayworld.cloud.module.item.calendar.resource.CoreCalendarResource;
import com.sunwayworld.cloud.module.item.calendar.service.CoreCalendarService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u65e5\u5386")
@RestController
@GikamBean
public class CoreCalendarResourceImpl
implements CoreCalendarResource,
AbstractGenericResource<CoreCalendarService, CoreCalendarBean, String> {
    @Autowired
    private CoreCalendarService calendarService;

    @Override
    public CoreCalendarService getService() {
        return this.calendarService;
    }

    @Override
    @Log(value="\u7f16\u8f91\u9875\u67e5\u8be2", type=LogType.SELECT)
    public ArrayList<CoreCalendarRepresentation> selectCalendarList(RestJsonWrapperBean wrapper) {
        return (ArrayList)this.getService().selectCalendarList(wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u64cd\u4f5c", type=LogType.UPDATE)
    public void save(int year, int month, int dayOfMonth, String dayRepresentation) {
        this.getService().save(year, month, dayOfMonth, dayRepresentation);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5de5\u4f5c\u65e5\u5929\u6570", type=LogType.SELECT)
    public Long getWorkDays(RestJsonWrapperBean wrapper) {
        return this.getService().getWorkDays(wrapper);
    }
}
