/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.listener;

import com.sunwayworld.cloud.module.item.calendar.service.CoreCalendarService;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class ItemReadyListener
implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private CoreCalendarService calendarService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.calendarService.selectAll().forEach(c -> RedisHelper.put("T_CORE_CALENDAR", c.getId(), c));
    }
}
