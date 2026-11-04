/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.help.impl;

import com.sunwayworld.cloud.module.item.controller.help.CoreHelpController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreHelpControllerImpl
implements CoreHelpController {
    @Override
    public String detailPage() {
        return "module/item/helps/help-detail";
    }
}
