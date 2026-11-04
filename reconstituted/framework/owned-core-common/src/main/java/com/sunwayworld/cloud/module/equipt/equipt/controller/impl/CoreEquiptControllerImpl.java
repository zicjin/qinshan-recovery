/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.equipt.controller.impl;

import com.sunwayworld.cloud.module.equipt.equipt.controller.CoreEquiptController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u5173\u952e\u4eea\u5668\u7ba1\u7406")
@Controller
@GikamBean
public class CoreEquiptControllerImpl
implements CoreEquiptController {
    @Override
    @Log(value="\u5173\u952e\u4eea\u5668\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/equipt/equipts/core-equipt-edit-list";
    }

    @Override
    @Log(value="\u5173\u952e\u4eea\u5668\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/equipt/equipts/core-equipt-detail";
    }

    @Override
    @Log(value="\u4eea\u5668\u76d1\u63a7\u9875", type=LogType.CONTROLLER)
    public String monitorPage() {
        return "module/equipt/equipts/core-equipt-monitor";
    }
}
