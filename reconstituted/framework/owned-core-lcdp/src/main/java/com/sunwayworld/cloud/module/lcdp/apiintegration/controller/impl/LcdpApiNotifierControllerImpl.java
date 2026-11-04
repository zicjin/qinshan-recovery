/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.controller.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.controller.LcdpApiNotifierController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class LcdpApiNotifierControllerImpl
implements LcdpApiNotifierController {
    @Override
    public String editListPage() {
        return "module/lcdp/api-integrations/lcdp-api-notifier-edit-list";
    }
}
