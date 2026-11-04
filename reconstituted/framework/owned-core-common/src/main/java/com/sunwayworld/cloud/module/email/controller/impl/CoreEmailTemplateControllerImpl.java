/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.controller.impl;

import com.sunwayworld.cloud.module.email.controller.CoreEmailTemplateController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreEmailTemplateControllerImpl
implements CoreEmailTemplateController {
    @Override
    public String editListPage() {
        return "module/item/email-templates/core-email-template-edit-list";
    }

    @Override
    public String detailPage() {
        return "module/item/email-templates/core-email-template-detail";
    }

    @Override
    public String choosePage() {
        return "module/item/email-templates/core-email-template-choose-list";
    }
}
