/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.sms.controller.CoreSmsController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreSmsControllerImpl
implements CoreSmsController {
    @Override
    public String searchListPage() {
        return "module/sys/sms/core-sms-search-list";
    }
}
