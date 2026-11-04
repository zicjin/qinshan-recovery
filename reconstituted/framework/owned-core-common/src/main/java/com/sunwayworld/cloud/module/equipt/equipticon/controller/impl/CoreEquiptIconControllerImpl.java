/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.equipticon.controller.impl;

import com.sunwayworld.cloud.module.equipt.equipticon.controller.CoreEquiptIconController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u4eea\u5668\u56fe\u6807")
@Controller
@GikamBean
public class CoreEquiptIconControllerImpl
implements CoreEquiptIconController {
    @Override
    @Log(value="\u4eea\u5668\u56fe\u6807\u7ba1\u7406\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/equipt/icons/core-equipt-icon-edit-list";
    }
}
