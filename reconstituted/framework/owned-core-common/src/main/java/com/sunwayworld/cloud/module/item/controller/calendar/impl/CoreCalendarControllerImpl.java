/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.calendar.impl;

import com.sunwayworld.cloud.module.item.controller.calendar.CoreCalendarController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreCalendarControllerImpl
implements CoreCalendarController {
    @Override
    @Log(value="\u65e5\u5386\u9875", type=LogType.CONTROLLER)
    public String mainPage() {
        return "module/item/calendars/main";
    }
}
