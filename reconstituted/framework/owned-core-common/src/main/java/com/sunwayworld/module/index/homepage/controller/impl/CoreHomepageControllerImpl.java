/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.index.homepage.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.index.homepage.controller.CoreHomepageController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreHomepageControllerImpl
implements CoreHomepageController {
    @Override
    @Log(value="\u9996\u9875", type=LogType.CONTROLLER)
    public String indexPage() {
        return "module/index/homepage/index";
    }

    @Override
    @Log(value="\u9996\u9875\u9759\u6001\u8d44\u6e90\u53ca\u5168\u5c40\u53c2\u6570\u914d\u7f6e", type=LogType.CONTROLLER)
    public String resourcePage() {
        return "module/gikam/home";
    }
}
