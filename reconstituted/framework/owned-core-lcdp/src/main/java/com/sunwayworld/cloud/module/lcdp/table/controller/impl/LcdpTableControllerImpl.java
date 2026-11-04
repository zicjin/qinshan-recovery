/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.controller.impl;

import com.sunwayworld.cloud.module.lcdp.table.controller.LcdpTableController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class LcdpTableControllerImpl
implements LcdpTableController {
    @Override
    @Log(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u8868\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    @RequestMapping(value={"/page-choose-list"})
    public String choosePage() {
        return "module/lcdp/tables/lcdp-table-choose-list";
    }
}
