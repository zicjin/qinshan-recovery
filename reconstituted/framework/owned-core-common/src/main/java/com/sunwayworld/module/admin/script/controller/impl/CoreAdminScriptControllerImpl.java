/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.script.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.script.controller.CoreAdminScriptController;
import org.springframework.stereotype.Controller;

@GikamBean
@Controller
public class CoreAdminScriptControllerImpl
implements CoreAdminScriptController {
    @Override
    public String detailPage() {
        return "module/admin/scripts/core-script-detail";
    }
}
