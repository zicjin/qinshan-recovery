/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.controller.impl;

import com.sunwayworld.cloud.module.email.controller.CoreEmailConfigController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreEmailConfigControllerImpl
implements CoreEmailConfigController {
    @Override
    public String editListPage() {
        return "module/item/email-configs/core-email-config-edit-list";
    }

    @Override
    public String detailPage() {
        return "module/item/email-configs/core-email-config-detail";
    }
}
