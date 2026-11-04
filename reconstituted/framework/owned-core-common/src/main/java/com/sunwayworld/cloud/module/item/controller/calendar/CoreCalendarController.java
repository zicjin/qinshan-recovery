/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.calendar;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/item/calendars/page"})
public interface CoreCalendarController {
    @RequestMapping(value={"/main"})
    public String mainPage();
}
