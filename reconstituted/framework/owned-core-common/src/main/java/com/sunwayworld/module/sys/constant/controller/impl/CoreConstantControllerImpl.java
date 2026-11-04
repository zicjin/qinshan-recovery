/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.constant.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.constant.controller.CoreConstantController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreConstantControllerImpl
implements CoreConstantController {
    @Override
    public String editListPage() {
        return "module/sys/constants/core-constant-edit-list";
    }

    @Override
    public String detailPage() {
        return "module/sys/constants/core-constant-detail";
    }
}
