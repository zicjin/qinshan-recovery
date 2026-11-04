/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.controller.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.controller.LcdpApiIntegrationController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class LcdpApiIntegrationControllerImpl
implements LcdpApiIntegrationController {
    @Override
    public String editListPage() {
        return "module/lcdp/api-integrations/lcdp-api-integration-edit-list";
    }

    @Override
    public String logPage() {
        return "module/lcdp/api-integrations/lcdp-api-integration-log-list";
    }
}
