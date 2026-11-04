/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.designer.controller.impl;

import com.sunwayworld.cloud.module.lcdp.designer.controller.LcdpDesignerController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class LcdpDesignerControllerImpl
implements LcdpDesignerController {
    @Override
    @Log(value="\u9996\u9875", type=LogType.CONTROLLER)
    @RequestMapping(value={"/index"})
    public String indexPage() {
        return "module/lcdp/designers/designer";
    }

    @Override
    @Log(value="\u8fd0\u884c\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/run"})
    public String runPage() {
        return "module/lcdp/designers/designer-run";
    }
}
