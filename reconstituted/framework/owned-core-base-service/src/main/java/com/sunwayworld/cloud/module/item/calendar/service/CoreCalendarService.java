/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.calendar.service;

import com.sunwayworld.cloud.module.item.calendar.bean.CoreCalendarBean;
import com.sunwayworld.cloud.module.item.calendar.bean.CoreCalendarRepresentation;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CoreCalendarService
extends GenericService<CoreCalendarBean, String> {
    public List<CoreCalendarRepresentation> selectCalendarList(RestJsonWrapperBean var1);

    public void save(int var1, int var2, int var3, String var4);

    public LocalDateTime plusDays(LocalDateTime var1, int var2);

    public LocalDateTime plusWorkingDays(LocalDateTime var1, int var2);

    public LocalDate plusDays(LocalDate var1, int var2);

    public LocalDate plusWorkingDays(LocalDate var1, int var2);

    public boolean isHoliday(LocalDateTime var1);

    public boolean isHoliday(LocalDate var1);

    public Long getWorkDays(RestJsonWrapperBean var1);
}
