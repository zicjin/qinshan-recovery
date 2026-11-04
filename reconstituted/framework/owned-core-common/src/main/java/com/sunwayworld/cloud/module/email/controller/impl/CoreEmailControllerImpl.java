/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.controller.impl;

import com.sunwayworld.cloud.module.email.controller.CoreEmailController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreEmailControllerImpl
implements CoreEmailController {
    @Override
    public String searchListPage() {
        return "module/item/emails/core-email-search-list";
    }

    @Override
    public String detailPage() {
        return "module/item/emails/core-email-detail";
    }
}
