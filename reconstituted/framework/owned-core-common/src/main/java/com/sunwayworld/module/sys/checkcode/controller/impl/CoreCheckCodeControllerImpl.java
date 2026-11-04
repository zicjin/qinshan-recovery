/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.checkcode.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.checkcode.controller.CoreCheckCodeController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreCheckCodeControllerImpl
implements CoreCheckCodeController {
    @Override
    @Log(value="\u590d\u9009\u6846\u7f16\u7801\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/check-codes/core-check-code-edit-list";
    }
}
