/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.password.controller.CorePasswordPolicyController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CorePasswordPolicyControllerImpl
implements CorePasswordPolicyController {
    @Override
    public String editListPage() {
        return "module/sys/password-policies/core-password-policy-edit-list";
    }

    @Override
    public String detailPage() {
        return "module/sys/password-policies/core-password-policy-detail";
    }
}
