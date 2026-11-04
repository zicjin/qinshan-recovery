/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.controller.impl;

import com.sunwayworld.cloud.module.lcdp.resource.controller.LcdpResourceController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class LcdpResourceControllerImpl
implements LcdpResourceController {
    @Override
    @Log(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u9875\u9762\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    @RequestMapping(value={"/page-choose-list"})
    public String pageChoosePage() {
        return "module/lcdp/resources/lcdp-resource-page-choose-list";
    }

    @Override
    @Log(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u811a\u672c\u63a5\u53e3\u65b9\u6cd5\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    @RequestMapping(value={"/method-choose-list"})
    public String methodChoosePage() {
        return "module/lcdp/resources/lcdp-resource-method-choose-list";
    }
}
