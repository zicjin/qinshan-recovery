/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.request.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.request.controller.CoreRequestUrlController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreRequestUrlControllerImpl
implements CoreRequestUrlController {
    @Override
    @Log(value="\u8def\u5f84\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/admin/request-urls/core-request-url-edit-list";
    }
}
