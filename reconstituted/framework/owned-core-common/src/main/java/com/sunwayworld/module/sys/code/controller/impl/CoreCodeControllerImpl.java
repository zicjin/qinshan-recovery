/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.code.controller.CoreCodeController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreCodeControllerImpl
implements CoreCodeController {
    @Override
    public String editListPage() {
        return "module/sys/codes/core-code-edit-list";
    }

    @Override
    public String detailPage() {
        return "module/sys/codes/core-code-detail";
    }

    @Override
    public String choosePage() {
        return "module/sys/codes/core-code-choose-list";
    }
}
