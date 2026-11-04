/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.controller.CoreI18nController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreI18nControllerImpl
implements CoreI18nController {
    @Override
    public String editListPage() {
        return "module/sys/i18ns/core-i18n-edit-list";
    }
}
